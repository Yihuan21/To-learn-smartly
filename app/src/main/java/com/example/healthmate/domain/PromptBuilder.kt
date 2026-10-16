package com.example.healthmate.domain

import com.example.healthmate.data.local.DailyLogEntity
import com.example.healthmate.data.local.UserProfileEntity
import com.example.healthmate.data.remote.ChatMessage
import com.google.gson.Gson

object PromptBuilder {
    private val gson = Gson()

    fun build(stableSummary: String, profile: UserProfileEntity?, newLog: DailyLogEntity): List<ChatMessage> {
        val fallback = profile?.let {
            "年龄：${if (it.age > 0) it.age else "未知"}；性别：${it.gender.ifBlank { "未知" }}；" +
                "身高cm：${if (it.heightCm > 0) it.heightCm else "未知"}；体重kg：${if (it.weightKg > 0) it.weightKg else "未知"}；" +
                "目标：${it.goal}；慢性病：${it.chronicConditions.ifBlank { "未提供" }}；" +
                "用药：${it.medications.ifBlank { "未提供" }}；过敏：${it.allergies.ifBlank { "未提供" }}"
        } ?: "尚未提供稳定健康档案；缺失信息应视为未知。"

        val summary = stableSummary.takeIf { it.isNotBlank() } ?: fallback
        return listOf(
            ChatMessage("system", HealthSystemPrompt.VALUE.trim()),
            ChatMessage("user", "【稳定健康档案摘要】\n$summary\n缺失值表示未知，不要自行推断。"),
            ChatMessage("user", "【本次新增记录】\n${gson.toJson(newLog)}\n请增量分析，仅依据新增记录更新摘要、评估、风险和建议；严格输出 system 中要求的 JSON。")
        )
    }
}
