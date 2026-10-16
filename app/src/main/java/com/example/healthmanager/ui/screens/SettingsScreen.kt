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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.healthmanager.data.local.UserProfileEntity
import com.example.healthmanager.ui.HealthViewModel

@Composable
fun SettingsScreen(viewModel: HealthViewModel) {
    val profile by viewModel.profile.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle()
    var age by rememberSaveable { mutableStateOf("") }
    var sex by rememberSaveable { mutableStateOf("") }
    var height by rememberSaveable { mutableStateOf("") }
    var weight by rememberSaveable { mutableStateOf("") }
    var goal by rememberSaveable { mutableStateOf("") }
    var conditions by rememberSaveable { mutableStateOf("") }
    var medications by rememberSaveable { mutableStateOf("") }
    var allergies by rememberSaveable { mutableStateOf("") }
    var diet by rememberSaveable { mutableStateOf("") }
    var exercise by rememberSaveable { mutableStateOf("") }
    var sleep by rememberSaveable { mutableStateOf("") }
    var stress by rememberSaveable { mutableStateOf("") }
    var consent by rememberSaveable { mutableStateOf(false) }
    var apiKey by rememberSaveable { mutableStateOf("") }
    var showClearDialog by remember { mutableStateOf(false) }

    LaunchedEffect(profile?.updatedAt) {
        profile?.let {
            age = it.age?.toString().orEmpty()
            sex = it.sex
            height = it.heightCm?.toString().orEmpty()
            weight = it.weightKg?.toString().orEmpty()
            goal = it.goal
            conditions = it.chronicConditions
            medications = it.medications
            allergies = it.allergies
            diet = it.dietPreferences
            exercise = it.exerciseBaseline
            sleep = it.sleepHabits
            stress = it.stressLevel
            consent = it.remoteAiConsent
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("设置与隐私", style = MaterialTheme.typography.headlineMedium)
        Text("健康档案保存在本机。只有你明确同意且主动请求分析时，相关档案与新增记录才会发送给已配置的远端 AI 服务。")
        OutlinedTextField(age, { age = it }, label = { Text("年龄（可选）") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(sex, { sex = it }, label = { Text("性别（可选）") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(height, { height = it }, label = { Text("身高 cm（可选）") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(weight, { weight = it }, label = { Text("体重 kg（可选）") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(goal, { goal = it }, label = { Text("健康目标") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(conditions, { conditions = it }, label = { Text("慢性病/健康状况（可选）") }, minLines = 2, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(medications, { medications = it }, label = { Text("用药情况（可选）") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(allergies, { allergies = it }, label = { Text("过敏情况（可选）") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(diet, { diet = it }, label = { Text("饮食偏好/限制") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(exercise, { exercise = it }, label = { Text("运动基础") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(sleep, { sleep = it }, label = { Text("作息习惯") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(stress, { stress = it }, label = { Text("压力水平（自述）") }, modifier = Modifier.fillMaxWidth())
        Button(
            onClick = {
                viewModel.saveProfile(
                    UserProfileEntity(
                        id = 1,
                        age = age.toIntOrNull()?.takeIf { it in 1..120 },
                        sex = sex.trim(),
                        heightCm = height.toDoubleOrNull()?.takeIf { it in 30.0..260.0 },
                        weightKg = weight.toDoubleOrNull()?.takeIf { it in 1.0..500.0 },
                        goal = goal.trim(),
                        chronicConditions = conditions.trim(),
                        medications = medications.trim(),
                        allergies = allergies.trim(),
                        dietPreferences = diet.trim(),
                        exerciseBaseline = exercise.trim(),
                        sleepHabits = sleep.trim(),
                        stressLevel = stress.trim(),
                        remoteAiConsent = consent
                    )
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("保存健康档案") }

        Text("云端 AI 分析", style = MaterialTheme.typography.titleLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = consent, onCheckedChange = { consent = it })
            Text("我已阅读说明，并同意将当前健康档案和本次新增记录发送给所配置的 AI 服务。")
        }
        Text("服务提供方可能按其政策处理请求数据。不要填写不必要的身份信息；此同意可随时关闭。")
        OutlinedTextField(
            value = apiKey,
            onValueChange = { apiKey = it },
            label = { Text("API Key（加密存储，不回显）") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                viewModel.saveProfile(
                    (profile ?: UserProfileEntity()).copy(remoteAiConsent = consent)
                )
                if (apiKey.isNotBlank()) viewModel.saveApiKey(apiKey)
                apiKey = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) { Text("保存同意状态与 API Key") }

        Text("数据控制", style = MaterialTheme.typography.titleLarge)
        Button(onClick = viewModel::shareExport, modifier = Modifier.fillMaxWidth()) {
            Text("导出数据（系统分享）")
        }
        Button(onClick = { showClearDialog = true }, modifier = Modifier.fillMaxWidth()) {
            Text("清除全部本地数据和 API Key")
        }
        if (status != null) Text(status ?: "", color = MaterialTheme.colorScheme.primary)

        if (showClearDialog) {
            AlertDialog(
                onDismissRequest = { showClearDialog = false },
                title = { Text("确认清除？") },
                text = { Text("此操作会删除本机档案、日志、报告、提醒和 API Key，且无法撤销。已导出的副本不受影响。") },
                confirmButton = {
                    TextButton(onClick = {
                        showClearDialog = false
                        viewModel.clearAllData()
                    }) { Text("清除") }
                },
                dismissButton = {
                    TextButton(onClick = { showClearDialog = false }) { Text("取消") }
                }
            )
        }
    }
}
