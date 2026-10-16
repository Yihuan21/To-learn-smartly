package com.example.healthmate.ui

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.healthmate.data.local.DailyLogEntity
import com.example.healthmate.data.local.UserProfileEntity
import java.time.LocalDate
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(vm: HealthViewModel, modifier: Modifier = Modifier) {
    val profile by vm.profile.collectAsStateWithLifecycle()
    val logs by vm.logs.collectAsStateWithLifecycle()
    val reminders by vm.reminders.collectAsStateWithLifecycle()
    val message by vm.message.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    var age by remember(profile) { mutableStateOf(profile?.age?.takeIf { it > 0 }?.toString().orEmpty()) }
    var gender by remember(profile) { mutableStateOf(profile?.gender?.takeIf { it != "未填写" }.orEmpty()) }
    var height by remember(profile) { mutableStateOf(profile?.heightCm?.takeIf { it > 0 }?.toString().orEmpty()) }
    var weight by remember(profile) { mutableStateOf(profile?.weightKg?.takeIf { it > 0 }?.toString().orEmpty()) }
    var goal by remember(profile) { mutableStateOf(profile?.goal.orEmpty()) }
    var chronic by remember(profile) { mutableStateOf(profile?.chronicConditions.orEmpty()) }
    var meds by remember(profile) { mutableStateOf(profile?.medications.orEmpty()) }
    var allergies by remember(profile) { mutableStateOf(profile?.allergies.orEmpty()) }
    var keyText by remember { mutableStateOf("") }
    var confirmClear by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text("今日健康概览", style = MaterialTheme.typography.headlineSmall)
            Text("健康记录默认留在本机；AI 分析是可选的。")
        }
        item {
            Card { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("健康档案", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(age, { age = it.filter(Char::isDigit).take(3) }, Modifier.weight(1f), label = { Text("年龄") }, singleLine = true)
                    OutlinedTextField(gender, { gender = it.take(30) }, Modifier.weight(1f), label = { Text("性别（可选）") }, singleLine = true)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(height, { height = it.take(6) }, Modifier.weight(1f), label = { Text("身高 cm") }, singleLine = true)
                    OutlinedTextField(weight, { weight = it.take(6) }, Modifier.weight(1f), label = { Text("体重 kg") }, singleLine = true)
                }
                OutlinedTextField(goal, { goal = it.take(200) }, Modifier.fillMaxWidth(), label = { Text("健康目标") })
                OutlinedTextField(chronic, { chronic = it.take(500) }, Modifier.fillMaxWidth(), label = { Text("慢性病/健康限制（可选）") })
                OutlinedTextField(meds, { meds = it.take(500) }, Modifier.fillMaxWidth(), label = { Text("用药（可选）") })
                OutlinedTextField(allergies, { allergies = it.take(500) }, Modifier.fillMaxWidth(), label = { Text("过敏/饮食限制（可选）") })
                Button(enabled = !busy, onClick = {
                    vm.saveProfile(UserProfileEntity(
                        age = age.toIntOrNull() ?: 0, gender = gender.ifBlank { "未填写" },
                        heightCm = height.toDoubleOrNull()?.takeIf { it in 30.0..260.0 } ?: 0.0,
                        weightKg = weight.toDoubleOrNull()?.takeIf { it in 2.0..500.0 } ?: 0.0,
                        goal = goal.ifBlank { "建立可持续的健康生活习惯" },
                        chronicConditions = chronic, medications = meds, allergies = allergies
                    ))
                }) { Text("保存档案") }
            } }
        }
        item {
            Card { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("AI 配置", style = MaterialTheme.typography.titleLarge)
                Text("默认使用 OpenAI 兼容接口。正式发布的 App 建议通过自有后端代理调用模型。")
                OutlinedTextField(keyText, { keyText = it }, Modifier.fillMaxWidth(), label = { Text("API Key（留空可清除）") }, visualTransformation = PasswordVisualTransformation(), singleLine = true)
                Button(enabled = !busy, onClick = { vm.saveApiKey(keyText); keyText = "" }) { Text("加密保存 / 清除密钥") }
            } }
        }
        item {
            Card { Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("本地数据", style = MaterialTheme.typography.titleLarge)
                Text("每日记录：${logs.size}   已启用提醒：${reminders.count { it.enabled }}")
                Text("最近记录：${logs.firstOrNull()?.date ?: "暂无"}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { scope.launch {
                        val json = vm.exportData()
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "application/json"
                            putExtra(Intent.EXTRA_SUBJECT, "HealthMate 本地数据导出")
                            putExtra(Intent.EXTRA_TEXT, json)
                        }
                        context.startActivity(Intent.createChooser(intent, "导出 JSON"))
                    } }) { Text("导出 JSON") }
                    OutlinedButton(enabled = !busy, onClick = { confirmClear = true }) { Text("清除所有数据") }
                }
            } }
        }
        if (message != null) item { AssistChip(onClick = { vm.dismissMessage() }, label = { Text(message ?: "") }) }
    }

    if (confirmClear) AlertDialog(
        onDismissRequest = { confirmClear = false },
        title = { Text("清除所有本机数据？") },
        text = { Text("将删除健康档案、日志、AI 报告、提醒与 API Key，操作无法撤销。建议先导出备份。") },
        confirmButton = { TextButton(onClick = { confirmClear = false; vm.clearAllData() }) { Text("确认清除") } },
        dismissButton = { TextButton(onClick = { confirmClear = false }) { Text("取消") } }
    )
}

@Composable
fun LogScreen(vm: HealthViewModel, modifier: Modifier = Modifier) {
    val message by vm.message.collectAsStateWithLifecycle()
    val busy by vm.busy.collectAsStateWithLifecycle()
    var sleep by remember { mutableStateOf("7.5") }
    var quality by remember { mutableFloatStateOf(3f) }
    var meals by remember { mutableStateOf("") }
    var exercise by remember { mutableStateOf("") }
    var minutes by remember { mutableStateOf("0") }
    var mood by remember { mutableStateOf("") }
    var symptoms by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var water by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var smoking by remember { mutableStateOf(false) }
    var alcohol by remember { mutableStateOf(false) }
    var askConsent by remember { mutableStateOf(false) }

    val makeLog = {
        DailyLogEntity(
            date = LocalDate.now().toString(),
            sleepHours = sleep.toDoubleOrNull()?.coerceIn(0.0, 24.0) ?: 0.0,
            sleepQuality = quality.toInt().coerceIn(1, 5), meals = meals.take(2000),
            exerciseType = exercise.take(200), exerciseMinutes = minutes.toIntOrNull()?.coerceIn(0, 1440) ?: 0,
            mood = mood.take(200), symptoms = symptoms.take(1000),
            weightKg = weight.toDoubleOrNull()?.takeIf { it in 2.0..500.0 },
            smoking = smoking, alcohol = alcohol, waterLiters = water.toDoubleOrNull()?.takeIf { it in 0.0..20.0 },
            notes = notes.take(2000)
        )
    }

    Column(modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("记录今日状态", style = MaterialTheme.typography.headlineSmall)
        Text("你可以仅保存在本机；AI 分析会在确认后发送档案摘要和本次记录。")
        OutlinedTextField(sleep, { sleep = it.take(5) }, Modifier.fillMaxWidth(), label = { Text("睡眠时长（小时）") }, singleLine = true)
        Text("睡眠质量：${quality.toInt()}/5")
        Slider(value = quality, onValueChange = { quality = it }, valueRange = 1f..5f, steps = 3)
        OutlinedTextField(meals, { meals = it }, Modifier.fillMaxWidth(), label = { Text("饮食记录") }, minLines = 2)
        OutlinedTextField(exercise, { exercise = it }, Modifier.fillMaxWidth(), label = { Text("运动类型") }, singleLine = true)
        OutlinedTextField(minutes, { minutes = it.filter(Char::isDigit).take(4) }, Modifier.fillMaxWidth(), label = { Text("运动分钟数") }, singleLine = true)
        OutlinedTextField(mood, { mood = it }, Modifier.fillMaxWidth(), label = { Text("情绪/压力") })
        OutlinedTextField(symptoms, { symptoms = it }, Modifier.fillMaxWidth(), label = { Text("症状（如有）") }, minLines = 2)
        OutlinedTextField(weight, { weight = it.take(6) }, Modifier.fillMaxWidth(), label = { Text("体重 kg（可选）") }, singleLine = true)
        OutlinedTextField(water, { water = it.take(5) }, Modifier.fillMaxWidth(), label = { Text("饮水 L（可选）") }, singleLine = true)
        Row { Checkbox(smoking, { smoking = it }); Text("吸烟"); Spacer(Modifier.width(24.dp)); Checkbox(alcohol, { alcohol = it }); Text("饮酒") }
        OutlinedTextField(notes, { notes = it }, Modifier.fillMaxWidth(), label = { Text("备注（可选）") }, minLines = 2)
        Button(onClick = { vm.saveLogLocally(makeLog()) }, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text("仅本地保存") }
        Button(onClick = { askConsent = true }, enabled = !busy, modifier = Modifier.fillMaxWidth()) { Text("保存并请求 AI 增量分析") }
        if (message != null) Text(message ?: "")
    }

    if (askConsent) AlertDialog(
        onDismissRequest = { askConsent = false },
        title = { Text("确认发送数据进行分析") },
        text = { Text("稳定健康档案摘要和本次新记录将发送到已配置的第三方 LLM 服务。请确认你已阅读该服务的隐私条款；你也可以取消并仅保存在本机。") },
        confirmButton = { TextButton(onClick = { askConsent = false; vm.saveLogAndAnalyze(makeLog()) }) { Text("同意并分析") } },
        dismissButton = { TextButton(onClick = { askConsent = false }) { Text("取消") } }
    )
}

@Composable
fun ReportScreen(vm: HealthViewModel, modifier: Modifier = Modifier) {
    val advice by vm.advice.collectAsStateWithLifecycle()
    Column(modifier.fillMaxSize().padding(16.dp)) {
        Text("历史分析报告", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        if (advice.isEmpty()) Text("暂无 AI 报告。可在记录页保存数据并主动请求增量分析。")
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(advice, key = { it.id }) { item ->
                Card { Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("日期：${item.date}", style = MaterialTheme.typography.titleMedium)
                    Text(item.rawJson, style = MaterialTheme.typography.bodySmall)
                } }
            }
        }
    }
}
