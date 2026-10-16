package com.example.healthmanager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        DailyLogEntity::class,
        HealthSummaryEntity::class,
        AiAdviceEntity::class,
        ReminderEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun dailyLogDao(): DailyLogDao
    abstract fun healthSummaryDao(): HealthSummaryDao
    abstract fun aiAdviceDao(): AiAdviceDao
    abstract fun reminderDao(): ReminderDao
}
