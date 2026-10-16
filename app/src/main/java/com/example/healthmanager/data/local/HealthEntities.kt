package com.example.healthmanager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Singleton profile: id is always 1. */
@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val age: Int? = null,
    val sex: String = "",
    val heightCm: Double? = null,
    val weightKg: Double? = null,
    val goal: String = "",
    val chronicConditions: String = "",
    val medications: String = "",
    val allergies: String = "",
    val dietPreferences: String = "",
    val exerciseBaseline: String = "",
    val sleepHabits: String = "",
    val stressLevel: String = "",
    val remoteAiConsent: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)

/** One record per local date; editing a date replaces that row. */
@Entity(tableName = "daily_log")
data class DailyLogEntity(
    @PrimaryKey val date: String,
    val sleepHours: Double? = null,
    val sleepQuality: String = "",
    val meals: String = "",
    val exerciseType: String = "",
    val exerciseMinutes: Int = 0,
    val exerciseIntensity: String = "",
    val mood: String = "",
    val symptoms: String = "",
    val weightKg: Double? = null,
    val smoking: Boolean = false,
    val alcohol: Boolean = false,
    val waterLiters: Double? = null,
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

/** Stable compact context for incremental analysis. */
@Entity(tableName = "health_summary")
data class HealthSummaryEntity(
    @PrimaryKey val id: Int = 1,
    val version: Int = 1,
    val summaryText: String,
    val updatedAt: Long = System.currentTimeMillis()
)

/** Latest generated report per date. Raw JSON is stored locally and never logged. */
@Entity(tableName = "ai_advice")
data class AiAdviceEntity(
    @PrimaryKey val date: String,
    val overallScore: Int? = null,
    val trend: String = "稳定",
    val summary: String = "",
    val adviceText: String = "",
    val rawJson: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminder")
data class ReminderEntity(
    @PrimaryKey val reminderId: String,
    val time: String,
    val repeatType: String,
    val title: String,
    val body: String,
    val enabled: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
