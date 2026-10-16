package com.example.healthmate.domain

import org.json.JSONObject

data class ReminderPayload(val id: String, val time: String, val repeat: String, val title: String, val body: String)
data class ParsedAnalysis(val summaryUpdate: String, val reminders: List<ReminderPayload>)

object AnalysisJson {
    fun parse(raw: String): ParsedAnalysis {
        val clean = raw.trim().removePrefix("```json").removePrefix("```").removeSuffix("```").trim()
        val json = JSONObject(clean)
        require(json.has("summary_update")) { "AI 返回缺少 summary_update" }
        require(json.optJSONObject("assessment_delta") != null) { "assessment_delta JSON 格式无效" }
        val values = json.optJSONArray("reminders")
        val reminders = buildList {
            if (values != null) for (i in 0 until values.length()) {
                val item = values.optJSONObject(i) ?: continue
                val time = item.optString("time", "")
                val repeat = item.optString("repeat", "daily")
                val title = item.optString("title", "").take(80)
                if (!time.matches(Regex("^(?:[01]\\d|2[0-3]):[0-5]\\d$")) ||
                    repeat !in setOf("daily", "once", "weekdays") || title.isBlank()) continue
                add(ReminderPayload(item.optString("id", "ai-$i").take(80), time, repeat, title, item.optString("body", "").take(240)))
            }
        }
        return ParsedAnalysis(json.optString("summary_update", "").take(500), reminders)
    }
}
