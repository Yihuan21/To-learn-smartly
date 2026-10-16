package com.example.healthmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.healthmanager.ui.HealthViewModel
import java.time.LocalDate

@Composable
fun HomeScreen(viewModel: HealthViewModel, onCreateLog: () -> Unit) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val reminders by viewModel.reminders.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle()
    val busy by viewModel.busy.collectAsStateWithLifecycle()
    val today = LocalDate.now().toString()
    val todayLog = logs.firstOrNull { it.date == today }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("今日健康概览", style = MaterialTheme.typography.headlineMedium)
            Text(today, style = MaterialTheme.typography.bodyMedium)
        }
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        if (profile == null) "请先建立健康档案"
                        else "目标：" + (profile?.goal?.ifBlank { "尚未设置" } ?: "尚未设置"),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text("已保存 " + logs.size + " 天记录")
                    Text(if (todayLog == null) "今天还没有记录" else "今天已记录，可按需请求增量分析")
                    Button(onClick = onCreateLog, modifier = Modifier.fillMaxWidth()) {
                        Text(if (todayLog == null) "记录今天" else "编辑今天的记录")
                    }
                    OutlinedButton(
                        onClick = { viewModel.analyzeDailyLog(today) },
                        enabled = todayLog != null && !busy,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (busy) CircularProgressIndicator() else Text("分析今天新增的数据")
                    }
                    Text("分析只在主动点击、已同意云端分析且已保存 API Key 后发送。")
                }
            }
        }
        if (status != null) item { Text(status ?: "", color = MaterialTheme.colorScheme.primary) }
        item { Text("启用中的提醒", style = MaterialTheme.typography.titleLarge) }
        if (reminders.none { it.enabled }) {
            item { Text("暂无提醒。完成分析后可以保存建议提醒。") }
        } else {
            items(reminders.filter { it.enabled }, key = { it.reminderId }) { reminder ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(reminder.time + " · " + reminder.title, style = MaterialTheme.typography.titleMedium)
                        Text(reminder.body)
                        Text(
                            when (reminder.repeatType) {
                                "weekdays" -> "工作日重复"
                                "once" -> "一次性"
                                else -> "每天重复"
                            }
                        )
                    }
                }
            }
        }
    }
}
