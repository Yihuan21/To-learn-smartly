package com.example.healthmate.data.repository

import android.content.Context
import androidx.room.withTransaction
import com.example.healthmate.data.local.*
import com.example.healthmate.data.remote.ChatCompletionRequest
import com.example.healthmate.data.remote.LlmApiService
import com.example.healthmate.data.security.ApiKeyStore
import com.example.healthmate.domain.AnalysisJson
import com.example.healthmate.domain.PromptBuilder
import com.example.healthmate.notification.ReminderScheduler
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class HealthRepository @Inject constructor(
    private val db: HealthDatabase,
    private val api: LlmApiService,
    private val keyStore: ApiKeyStore,
    @ApplicationContext private val context: Context
) {
    private val scheduler = ReminderScheduler(context)
    private val gson = Gson()
    val profile: Flow<UserProfileEntity?> = db.userProfileDao().observeProfile()
    val logs: Flow<List<DailyLogEntity>> = db.dailyLogDao().observeAll()
    val advice: Flow<List<AiAdviceEntity>> = db.aiAdviceDao().observeAll()
    val reminders: Flow<List<ReminderEntity>> = db.reminderDao().observeAll()

    suspend fun saveProfile(value: UserProfileEntity) = db.userProfileDao().upsert(value.copy(updatedAt = System.currentTimeMillis()))
    suspend fun saveLog(value: DailyLogEntity) = db.dailyLogDao().upsert(value)
    fun saveApiKey(value: String) = keyStore.save(value)

    suspend fun analyzeLog(log: DailyLogEntity) {
        val key = keyStore.read()?.takeIf { it.isNotBlank() } ?: error("请先在首页配置 API Key。")
        val profile = db.userProfileDao().getProfile()
        val prior = db.healthSummaryDao().getLatest()
        val request = ChatCompletionRequest(
            model = "gpt-4o-mini",
            messages = PromptBuilder.build(prior?.summaryText.orEmpty(), profile, log)
        )
        // Deliberately no HTTP logging interceptor: prompts contain sensitive health data.
        val raw = api.chatCompletions("Bearer $key", request).choices.firstOrNull()?.message?.content
            ?.takeIf { it.isNotBlank() } ?: error("AI 服务未返回有效内容。")
        val parsed = AnalysisJson.parse(raw)
        val reminderRows = parsed.reminders.map {
            ReminderEntity(it.id, it.time, it.repeat, it.title, it.body)
        }
        db.withTransaction {
            if (parsed.summaryUpdate.isNotBlank()) {
                db.healthSummaryDao().upsert(HealthSummaryEntity(version = (prior?.version ?: 0) + 1, summaryText = parsed.summaryUpdate))
            }
            db.aiAdviceDao().insert(AiAdviceEntity(date = log.date, rawJson = raw))
            reminderRows.forEach { db.reminderDao().upsert(it) }
        }
        reminderRows.forEach(scheduler::schedule)
    }

    suspend fun exportJson(): String = gson.toJson(
        mapOf(
            "schema_version" to 1,
            "exported_at_epoch_ms" to System.currentTimeMillis(),
            "profile" to db.userProfileDao().getProfile(),
            "logs" to db.dailyLogDao().getAll(),
            "summary" to db.healthSummaryDao().getLatest(),
            "reports" to db.aiAdviceDao().getAll(),
            "reminders" to db.reminderDao().getAll()
        )
    )

    suspend fun clearAllData() {
        db.reminderDao().getAll().forEach { scheduler.cancel(it.reminderId) }
        db.withTransaction {
            db.userProfileDao().deleteAll()
            db.dailyLogDao().deleteAll()
            db.healthSummaryDao().deleteAll()
            db.aiAdviceDao().deleteAll()
            db.reminderDao().deleteAll()
        }
        keyStore.clear()
    }
}
