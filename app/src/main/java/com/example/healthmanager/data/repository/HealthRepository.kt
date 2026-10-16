package com.example.healthmanager.data.repository

import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import com.example.healthmanager.data.local.AiAdviceEntity
import com.example.healthmanager.data.local.AppDatabase
import com.example.healthmanager.data.local.DailyLogEntity
import com.example.healthmanager.data.local.HealthSummaryEntity
import com.example.healthmanager.data.local.ReminderEntity
import com.example.healthmanager.data.local.UserProfileEntity
import com.example.healthmanager.data.remote.ApiKeyStore
import com.example.healthmanager.data.remote.ChatCompletionRequest
import com.example.healthmanager.data.remote.LlmApiService
import com.example.healthmanager.domain.HealthAnalysis
import com.example.healthmanager.domain.PromptBuilder
import com.example.healthmanager.reminder.ReminderScheduler
import com.squareup.moshi.JsonAdapter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthRepository @Inject constructor(
    private val database: AppDatabase,
    private val api: LlmApiService,
    private val apiKeyStore: ApiKeyStore,
    private val reminderScheduler: ReminderScheduler,
    private val analysisAdapter: JsonAdapter<HealthAnalysis>,
    @ApplicationContext private val context: Context
) {
    val profileDao get() = database.userProfileDao()
    val logDao get() = database.dailyLogDao()
    val adviceDao get() = database.aiAdviceDao()
    val reminderDao get() = database.reminderDao()

    suspend fun saveProfile(profile: UserProfileEntity) {
        database.userProfileDao().upsert(profile.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun saveLog(log: DailyLogEntity) {
        database.dailyLogDao().upsert(log.copy(updatedAt = System.currentTimeMillis()))
    }

    suspend fun saveApiKey(key: String) = withContext(Dispatchers.IO) {
        apiKeyStore.save(key)
    }

    /** Called only on explicit user action; requires stored privacy consent and API key. */
    suspend fun analyzeDailyLog(date: String): HealthAnalysis = withContext(Dispatchers.IO) {
        val profile = database.userProfileDao().getProfile()
            ?: throw IllegalStateException("请先填写健康档案。")
        if (!profile.remoteAiConsent) {
            throw IllegalStateException("请先阅读隐私说明并同意云端 AI 分析。")
        }
        val apiKey = apiKeyStore.read()
            ?: throw IllegalStateException("请先在设置中保存 API Key。")
        val log = database.dailyLogDao().getByDate(date)
            ?: throw IllegalStateException("没有找到该日期的健康记录。")

        val previous = database.healthSummaryDao().getLatest()
        val messages = PromptBuilder.buildMessages(previous?.summaryText.orEmpty(), profile, log)
        val response = api.chatCompletions(
            authorization = "Bearer " + apiKey,
            request = ChatCompletionRequest(model = DEFAULT_MODEL, messages = messages)
        )
        val rawJson = response.choices.firstOrNull()?.message?.content?.trim().orEmpty()
        if (rawJson.isBlank()) throw IOException("AI 服务返回了空内容。")
        val result = try {
            analysisAdapter.fromJson(rawJson)
        } catch (e: Exception) {
            throw IOException("AI 返回内容无法解析为约定 JSON，请检查模型兼容性后重试。", e)
        } ?: throw IOException("AI 返回了空分析结果。")

        // Update stable context only after a successful, structured response.
        if (result.summaryUpdate.isNotBlank()) {
            database.healthSummaryDao().upsert(
                HealthSummaryEntity(
                    id = 1,
                    version = (previous?.version ?: 0) + 1,
                    summaryText = result.summaryUpdate.take(500),
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

        val adviceText = result.advice.sortedBy { it.priority }.joinToString(
            separator = System.lineSeparator()
        ) { item ->
            buildString {
                append("【").append(item.category.ifBlank { "建议" }).append("】")
                append(item.content)
                if (item.expected.isNotBlank()) appendLine().append("预期观察：").append(item.expected)
                if (item.timeline.isNotBlank()) appendLine().append("建议周期：").append(item.timeline)
            }
        }

        database.aiAdviceDao().upsert(
            AiAdviceEntity(
                date = date,
                overallScore = result.assessmentDelta.overallScore?.coerceIn(0, 100),
                trend = result.assessmentDelta.trend,
                summary = result.summaryUpdate.take(500),
                adviceText = adviceText,
                rawJson = rawJson,
                createdAt = System.currentTimeMillis()
            )
        )

        result.reminders.forEach { suggestion ->
            val id = suggestion.id.ifBlank { UUID.randomUUID().toString() }
            val parts = suggestion.time.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull()
            val minute = parts.getOrNull(1)?.toIntOrNull()
            val validTime = parts.size == 2 && hour != null && minute != null &&
                hour in 0..23 && minute in 0..59
            val repeat = suggestion.repeat.lowercase().takeIf {
                it == "daily" || it == "once" || it == "weekdays"
            } ?: "daily"
            val reminder = ReminderEntity(
                reminderId = id,
                time = if (validTime) suggestion.time else "09:00",
                repeatType = repeat,
                title = suggestion.title.take(80),
                body = suggestion.body.take(300),
                enabled = true
            )
            database.reminderDao().upsert(reminder)
            reminderScheduler.schedule(reminder)
        }
        result
    }

    suspend fun exportJson(): String = withContext(Dispatchers.IO) {
        val moshi = com.squareup.moshi.Moshi.Builder()
            .add(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()
        val root = JSONObject()
        val profile = database.userProfileDao().getProfile()
        root.put(
            "profile",
            profile?.let { JSONObject(moshi.adapter(UserProfileEntity::class.java).toJson(it)) }
                ?: JSONObject.NULL
        )
        val logs = JSONArray()
        database.dailyLogDao().getAllOnce().forEach {
            logs.put(JSONObject(moshi.adapter(DailyLogEntity::class.java).toJson(it)))
        }
        root.put("dailyLogs", logs)
        val reports = JSONArray()
        database.aiAdviceDao().getAllOnce().forEach {
            reports.put(JSONObject(moshi.adapter(AiAdviceEntity::class.java).toJson(it)))
        }
        root.put("aiAdvice", reports)
        val reminders = JSONArray()
        database.reminderDao().getAllOnce().forEach {
            reminders.put(JSONObject(moshi.adapter(ReminderEntity::class.java).toJson(it)))
        }
        root.put("reminders", reminders)
        root.put("exportedAtEpochMillis", System.currentTimeMillis())
        root.toString(2)
    }

    suspend fun shareExport() {
        val export = exportJson()
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_SUBJECT, "安心健康数据导出")
            putExtra(Intent.EXTRA_TEXT, export)
        }
        context.startActivity(
            Intent.createChooser(sendIntent, "选择导出目标")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        database.reminderDao().getAllOnce().forEach { reminderScheduler.cancel(it.reminderId) }
        WorkManager.getInstance(context).cancelAllWork()
        database.clearAllTables()
        apiKeyStore.delete()
    }

    companion object {
        private const val DEFAULT_MODEL = "gpt-4o-mini"
    }
}
