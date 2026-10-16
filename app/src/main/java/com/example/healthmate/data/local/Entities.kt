package com.example.healthmate.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val age: Int = 0,
    val gender: String = "未填写",
    val heightCm: Double = 0.0,
    val weightKg: Double = 0.0,
    val goal: String = "建立可持续的健康生活习惯",
    val chronicConditions: String = "",
    val medications: String = "",
    val allergies: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_logs", indices = [Index(value = ["date"], unique = true)])
data class DailyLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val sleepHours: Double,
    val sleepQuality: Int = 3,
    val meals: String = "",
    val exerciseType: String = "",
    val exerciseMinutes: Int = 0,
    val exerciseIntensity: String = "轻度",
    val mood: String = "",
    val symptoms: String = "",
    val weightKg: Double? = null,
    val smoking: Boolean = false,
    val alcohol: Boolean = false,
    val waterLiters: Double? = null,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "health_summary")
data class HealthSummaryEntity(
    @PrimaryKey val id: Int = 1,
    val version: Int,
    val summaryText: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_advice", indices = [Index(value = ["date"])])
data class AiAdviceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val rawJson: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val reminderId: String,
    val time: String,
    val repeatType: String = "daily",
    val title: String,
    val body: String,
    val enabled: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis()
)
