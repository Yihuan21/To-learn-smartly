package com.example.healthmanager.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthmanager.ui.screens.HomeScreen
import com.example.healthmanager.ui.screens.LogScreen
import com.example.healthmanager.ui.screens.ReportScreen
import com.example.healthmanager.ui.screens.SettingsScreen

private enum class AppTab(val label: String) {
    Home("首页"), Log("记录"), Report("报告"), Settings("设置")
}

@Composable
fun HealthApp(viewModel: HealthViewModel = hiltViewModel()) {
    var selectedTab by remember { mutableStateOf(AppTab.Home) }
    Scaffold(
        bottomBar = {
            NavigationBar {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    AppTab.Home -> Icons.Default.Home
                                    AppTab.Log -> Icons.Default.NoteAdd
                                    AppTab.Report -> Icons.Default.Assessment
                                    AppTab.Settings -> Icons.Default.Settings
                                },
                                contentDescription = tab.label
                            )
                        },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                AppTab.Home -> HomeScreen(viewModel, onCreateLog = { selectedTab = AppTab.Log })
                AppTab.Log -> LogScreen(viewModel)
                AppTab.Report -> ReportScreen(viewModel)
                AppTab.Settings -> SettingsScreen(viewModel)
            }
        }
    }
}
