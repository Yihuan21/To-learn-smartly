package com.example.healthmanager.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmanager.data.local.DailyLogEntity
import com.example.healthmanager.data.local.UserProfileEntity
import com.example.healthmanager.data.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HealthViewModel @Inject constructor(
    private val repository: HealthRepository
) : ViewModel() {
    val profile = repository.profileDao.observeProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
    val logs = repository.logDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val advice = repository.adviceDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val reminders = repository.reminderDao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status.asStateFlow()
    private val _busy = MutableStateFlow(false)
    val busy: StateFlow<Boolean> = _busy.asStateFlow()

    fun saveProfile(profile: UserProfileEntity) {
        viewModelScope.launch {
            runCatching { repository.saveProfile(profile) }
                .onSuccess { _status.value = "档案已保存到本机。" }
                .onFailure { _status.value = "保存失败：" + (it.message ?: "未知错误") }
        }
    }

    fun saveLog(log: DailyLogEntity) {
        viewModelScope.launch {
            runCatching { repository.saveLog(log) }
                .onSuccess { _status.value = "记录已保存到本机；不会自动上传。" }
                .onFailure { _status.value = "记录保存失败：" + (it.message ?: "未知错误") }
        }
    }

    fun analyzeDailyLog(date: String) {
        viewModelScope.launch {
            _busy.value = true
            _status.value = "正在请求增量分析…"
            try {
                repository.analyzeDailyLog(date)
                _status.value = "分析完成，报告已保存。"
            } catch (t: Throwable) {
                _status.value = t.message ?: "分析失败，请检查网络与设置。"
            } finally {
                _busy.value = false
            }
        }
    }

    fun saveApiKey(key: String) {
        viewModelScope.launch {
            runCatching { repository.saveApiKey(key) }
                .onSuccess { _status.value = "API Key 已加密保存在本机。" }
                .onFailure { _status.value = "API Key 保存失败：" + (it.message ?: "未知错误") }
        }
    }

    fun shareExport() {
        viewModelScope.launch {
            runCatching { repository.shareExport() }
                .onFailure { _status.value = "导出失败：" + (it.message ?: "未知错误") }
                .onSuccess { _status.value = "已打开系统分享面板，请确认导出目标。" }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            runCatching { repository.clearAllData() }
                .onSuccess { _status.value = "本地健康数据与 API Key 已清除。" }
                .onFailure { _status.value = "清除失败：" + (it.message ?: "未知错误") }
        }
    }
}
