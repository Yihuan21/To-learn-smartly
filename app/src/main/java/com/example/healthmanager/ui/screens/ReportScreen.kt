package com.example.healthmanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.healthmanager.ui.HealthViewModel

@Composable
fun ReportScreen(viewModel: HealthViewModel) {
    val reports by viewModel.advice.collectAsStateWithLifecycle()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("健康报告", style = MaterialTheme.typography.headlineMedium) }
        item {
            Text(
                "报告只用于生活方式管理，不构成医学诊断。持续或严重症状请联系专业医务人员。",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        if (reports.isEmpty()) {
            item { Text("尚无报告。保存一条记录后，可在首页主动请求 AI 增量分析。") }
        } else {
            items(reports, key = { it.date }) { report ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(report.date, style = MaterialTheme.typography.labelLarge)
                        if (report.overallScore != null) {
                            Text("生活方式自我管理评分：" + report.overallScore + "/100", style = MaterialTheme.typography.titleMedium)
                        }
                        Text("趋势：" + report.trend)
                        if (report.summary.isNotBlank()) {
                            Text("稳定摘要", style = MaterialTheme.typography.titleSmall)
                            Text(report.summary)
                        }
                        if (report.adviceText.isNotBlank()) {
                            Text("建议", style = MaterialTheme.typography.titleSmall)
                            Text(report.adviceText)
                        }
                    }
                }
            }
        }
    }
}
