package com.example.healthmanager.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthmanager.data.local.AppDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject lateinit var database: AppDatabase
    @Inject lateinit var scheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            try {
                database.reminderDao().getEnabled().forEach(scheduler::schedule)
            } finally {
                pending.finish()
            }
        }
    }
}
