package com.example.healthmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.healthmanager.data.local.DailyLogEntity
import com.example.healthmanager.ui.HealthViewModel
import java.time.LocalDate

@Composable
fun LogScreen(viewModel: HealthViewModel) {
    val logs by viewModel.logs.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle()
    val today = LocalDate.now().toString()
    val existing = logs.firstOrNull { it.date == today }
    var sleep by remember(existing?.updatedAt) { mutableStateOf(existing?.sleepHours?.toString().orEmpty()) }
    var quality by remember(existing?.updatedAt) { mutableStateOf(existing?.sleepQuality.orEmpty()) }
    var meals by remember(existing?.updatedAt) { mutableStateOf(existing?.meals.orEmpty()) }
    var exercise by remember(existing?.updatedAt) { mutableStateOf(existing?.exerciseType.orEmpty()) }
    var minutes by remember(existing?.updatedAt) { mutableStateOf(existing?.exerciseMinutes?.takeIf { it > 0 }?.toString().orEmpty()) }
    var mood by remember(existing?.updatedAt) { mutableStateOf(existing?.mood.orEmpty()) }
    var symptoms by remember(existing?.updatedAt) { mutableStateOf(existing?.symptoms.orEmpty()) }
    var weight by remember(existing?.updatedAt) { mutableStateOf(existing?.weightKg?.toString().orEmpty()) }
    var water by remember(existing?.updatedAt) { mutableStateOf(existing?.waterLiters?.toString().orEmpty()) }
    var notes by remember(existing?.updatedAt) { mutableStateOf(existing?.notes.orEmpty()) }
    var smoking by remember(existing?.updatedAt) { mutableStateOf(existing?.smoking ?: false) }
    var alcohol by remember(existing?.updatedAt) { mutableStateOf(existing?.alcohol ?: false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("每日健康记录", style = MaterialTheme.typography.headlineMedium)
        Text("日期：" + today + "（同一天保存会覆盖该日记录）")
        OutlinedTextField(sleep, { sleep = it }, label = { Text("睡眠时长（小时）") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(quality, { quality = it }, label = { Text("睡眠质量（好/一般/差）") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(meals, { meals = it }, label = { Text("饮食记录") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(exercise, { exercise = it }, label = { Text("运动类型") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(minutes, { minutes = it }, label = { Text("运动时长（分钟）") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(mood, { mood = it }, label = { Text("今日情绪/压力") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(symptoms, { symptoms = it }, label = { Text("症状（没有可留空）") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = smoking, onCheckedChange = { smoking = it })
            Text("吸烟")
            Checkbox(checked = alcohol, onCheckedChange = { alcohol = it })
            Text("饮酒")
        }
        OutlinedTextField(weight, { weight = it }, label = { Text("体重 kg（可选）") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(water, { water = it }, label = { Text("饮水量升（可选）") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(notes, { notes = it }, label = { Text("补充备注") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                viewModel.saveLog(
                    DailyLogEntity(
                        date = today,
                        sleepHours = sleep.toDoubleOrNull()?.takeIf { it in 0.0..24.0 },
                        sleepQuality = quality.trim(),
                        meals = meals.trim(),
                        exerciseType = exercise.trim(),
                        exerciseMinutes = minutes.toIntOrNull()?.coerceIn(0, 1440) ?: 0,
                        mood = mood.trim(),
                        symptoms = symptoms.trim(),
                        weightKg = weight.toDoubleOrNull()?.takeIf { it in 1.0..500.0 },
                        smoking = smoking,
                        alcohol = alcohol,
                        waterLiters = water.toDoubleOrNull()?.takeIf { it in 0.0..30.0 },
                        notes = notes.trim()
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("保存到本机") }
        Text("空白数字字段会作为未记录保存。保存不会自动上传。", style = MaterialTheme.typography.bodySmall)
        if (status != null) Text(status ?: "", color = MaterialTheme.colorScheme.primary)
    }
}
