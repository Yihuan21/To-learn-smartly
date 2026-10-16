package com.example.healthmanager.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun observeProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getProfile(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: UserProfileEntity)
}

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_log ORDER BY date DESC")
    fun observeAll(): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_log WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): DailyLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(log: DailyLogEntity)

    @Query("SELECT * FROM daily_log ORDER BY date DESC")
    suspend fun getAllOnce(): List<DailyLogEntity>
}

@Dao
interface HealthSummaryDao {
    @Query("SELECT * FROM health_summary WHERE id = 1 LIMIT 1")
    suspend fun getLatest(): HealthSummaryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(summary: HealthSummaryEntity)
}

@Dao
interface AiAdviceDao {
    @Query("SELECT * FROM ai_advice ORDER BY date DESC")
    fun observeAll(): Flow<List<AiAdviceEntity>>

    @Query("SELECT * FROM ai_advice WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: String): AiAdviceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(advice: AiAdviceEntity)

    @Query("SELECT * FROM ai_advice ORDER BY date DESC")
    suspend fun getAllOnce(): List<AiAdviceEntity>
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminder ORDER BY time ASC")
    fun observeAll(): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminder WHERE enabled = 1")
    suspend fun getEnabled(): List<ReminderEntity>

    @Query("SELECT * FROM reminder")
    suspend fun getAllOnce(): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(reminder: ReminderEntity)

    @Query("DELETE FROM reminder WHERE reminderId = :id")
    suspend fun deleteById(id: String)
}
