package com.example.healthmanager.domain

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HealthAnalysis(
    @Json(name = "summary_update") val summaryUpdate: String = "",
    @Json(name = "assessment_delta") val assessmentDelta: AssessmentDelta = AssessmentDelta(),
    val risks: List<HealthRisk> = emptyList(),
    val advice: List<HealthAdvice> = emptyList(),
    @Json(name = "plan_updates") val planUpdates: PlanUpdates = PlanUpdates(),
    val reminders: List<ReminderSuggestion> = emptyList(),
    @Json(name = "need_more_info") val needMoreInfo: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AssessmentDelta(
    @Json(name = "overall_score") val overallScore: Int? = null,
    val trend: String = "稳定",
    val dimensions: Map<String, Int> = emptyMap(),
    @Json(name = "key_findings") val keyFindings: List<String> = emptyList()
)

@JsonClass(generateAdapter = true)
data class HealthRisk(
    val level: String = "低",
    val type: String = "",
    val reason: String = "",
    val action: String = ""
)

@JsonClass(generateAdapter = true)
data class HealthAdvice(
    val category: String = "",
    val priority: Int = 3,
    val content: String = "",
    val expected: String = "",
    val timeline: String = ""
)

@JsonClass(generateAdapter = true)
data class PlanUpdates(
    val daily: List<PlanItem> = emptyList(),
    val weekly: List<WeeklyPlanItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class PlanItem(
    val time: String = "",
    val activity: String = "",
    val detail: String = ""
)

@JsonClass(generateAdapter = true)
data class WeeklyPlanItem(
    val day: String = "",
    val activity: String = "",
    val detail: String = ""
)

@JsonClass(generateAdapter = true)
data class ReminderSuggestion(
    val id: String = "",
    val time: String = "09:00",
    val repeat: String = "daily",
    val title: String = "健康提醒",
    val body: String = ""
)
