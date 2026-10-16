package com.example.healthmate

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.healthmate.ui.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) permissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)

        setContent {
            MaterialTheme {
                val vm: HealthViewModel = hiltViewModel()
                var selected by remember { mutableStateOf(HealthScreen.Home) }
                Scaffold(bottomBar = {
                    NavigationBar {
                        HealthScreen.entries.forEach { page ->
                            NavigationBarItem(
                                selected = selected == page,
                                onClick = { selected = page },
                                icon = { Text(page.icon) },
                                label = { Text(page.label) }
                            )
                        }
                    }
                }) { padding ->
                    when (selected) {
                        HealthScreen.Home -> HomeScreen(vm, Modifier.padding(padding))
                        HealthScreen.Log -> LogScreen(vm, Modifier.padding(padding))
                        HealthScreen.Report -> ReportScreen(vm, Modifier.padding(padding))
                    }
                }
            }
        }
    }
}
