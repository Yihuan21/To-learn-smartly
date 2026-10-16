package com.example.healthmate.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmate.data.local.DailyLogEntity
import com.example.healthmate.data.local.UserProfileEntity
import com.example.healthmate.data.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class HealthViewModel @Inject constructor(private val repo: HealthRepository) : ViewModel() {
    val profile = repo.profile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    val logs = repo.logs.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val advice = repo.advice.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val reminders = repo.reminders.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private val _message = MutableStateFlow<String?>(null)
    val message = _message.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy = _busy.asStateFlow()

    fun saveProfile(value: UserProfileEntity) = runOperation { repo.saveProfile(value); _message.value = "档案已保存到本机。" }
    fun saveApiKey(value: String) = runOperation {
        repo.saveApiKey(value.trim()); _message.value = if (value.isBlank()) "API Key 已清除。" else "API Key 已加密保存在本机。"
    }
    fun saveLogLocally(value: DailyLogEntity) = runOperation { repo.saveLog(value); _message.value = "记录已仅保存在本机。" }
    fun saveLogAndAnalyze(value: DailyLogEntity) = runOperation {
        repo.saveLog(value)
        _message.value = "记录已保存在本机，正在增量分析……"
        try {
            repo.analyzeLog(value)
            _message.value = "分析完成，报告与提醒已保存。"
        } catch (e: Exception) {
            _message.value = "记录已本地保存，但 AI 分析失败：${e.message ?: "请检查网络和配置"}"
        }
    }
    suspend fun exportData(): String = repo.exportJson()
    fun clearAllData() = runOperation { repo.clearAllData(); _message.value = "本地档案、记录、报告、提醒和 API Key 已清除。" }
    fun dismissMessage() { _message.value = null }

    private fun runOperation(block: suspend () -> Unit) {
        viewModelScope.launch {
            _busy.value = true
            try { block() } catch (e: Exception) { _message.value = e.message ?: "操作失败，请重试。" }
            finally { _busy.value = false }
        }
    }
}

enum class HealthScreen(val label: String, val icon: String) {
    Home("首页", "⌂"), Log("记录", "+"), Report("报告", "▤")
}
