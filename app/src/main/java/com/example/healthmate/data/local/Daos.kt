package com.example.healthmate.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1") fun observeProfile(): Flow<UserProfileEntity?>
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1") suspend fun getProfile(): UserProfileEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(profile: UserProfileEntity)
    @Query("DELETE FROM user_profile") suspend fun deleteAll()
}

@Dao
interface DailyLogDao {
    @Query("SELECT * FROM daily_logs ORDER BY date DESC, id DESC") fun observeAll(): Flow<List<DailyLogEntity>>
    @Query("SELECT * FROM daily_logs ORDER BY date DESC, id DESC LIMIT 1") fun observeLatest(): Flow<DailyLogEntity?>
    @Query("SELECT * FROM daily_logs ORDER BY date DESC, id DESC") suspend fun getAll(): List<DailyLogEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(log: DailyLogEntity)
    @Query("DELETE FROM daily_logs") suspend fun deleteAll()
}

@Dao
interface HealthSummaryDao {
    @Query("SELECT * FROM health_summary WHERE id = 1 LIMIT 1") suspend fun getLatest(): HealthSummaryEntity?
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(summary: HealthSummaryEntity)
    @Query("DELETE FROM health_summary") suspend fun deleteAll()
}

@Dao
interface AiAdviceDao {
    @Query("SELECT * FROM ai_advice ORDER BY createdAt DESC") fun observeAll(): Flow<List<AiAdviceEntity>>
    @Query("SELECT * FROM ai_advice ORDER BY createdAt DESC") suspend fun getAll(): List<AiAdviceEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(advice: AiAdviceEntity)
    @Query("DELETE FROM ai_advice") suspend fun deleteAll()
}

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders ORDER BY time ASC") fun observeAll(): Flow<List<ReminderEntity>>
    @Query("SELECT * FROM reminders") suspend fun getAll(): List<ReminderEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsert(reminder: ReminderEntity)
    @Query("DELETE FROM reminders") suspend fun deleteAll()
}
