package com.example.healthmanager.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthmanager.data.local.ReminderEntity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {
    @Inject lateinit var scheduler: ReminderScheduler

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra("id") ?: return
        val time = intent.getStringExtra("time") ?: "09:00"
        val repeat = intent.getStringExtra("repeat") ?: "once"
        val title = intent.getStringExtra("title") ?: "健康提醒"
        val body = intent.getStringExtra("body") ?: ""
        NotificationHelper.show(context, id.hashCode(), title, body)
        if (!repeat.equals("once", ignoreCase = true)) {
            scheduler.schedule(
                ReminderEntity(id, time, repeat, title, body, enabled = true)
            )
        }
    }
}
