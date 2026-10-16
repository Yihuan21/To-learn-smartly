package com.example.healthmanager.domain

import com.example.healthmanager.data.local.DailyLogEntity
import com.example.healthmanager.data.local.UserProfileEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PromptBuilderTest {
    @Test
    fun fixedSystemPromptIsFirstAndNewLogIsPresent() {
        val profile = UserProfileEntity(age = 30, goal = "改善睡眠")
        val log = DailyLogEntity(
            date = "2026-10-17",
            sleepHours = 6.5,
            exerciseType = "步行",
            exerciseMinutes = 20,
            mood = "一般"
        )

        val messages = PromptBuilder.buildMessages("最近作息不规律", profile, log)

        assertEquals("system", messages.first().role)
        assertEquals(PromptBuilder.SYSTEM_PROMPT, messages.first().content)
        assertEquals("user", messages.last().role)
        assertTrue(messages.last().content.contains("2026-10-17"))
        assertTrue(messages.last().content.contains("最近作息不规律"))
        assertTrue(messages.last().content.contains("步行"))
    }
}
