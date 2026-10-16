package com.example.healthmate.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.healthmate.data.local.ReminderEntity

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(ReminderScheduler.TITLE) ?: return
        val id = intent.getStringExtra(ReminderScheduler.ID) ?: title.hashCode().toString()
        val time = intent.getStringExtra(ReminderScheduler.TIME) ?: "20:00"
        val repeat = intent.getStringExtra(ReminderScheduler.REPEAT) ?: "once"
        val body = intent.getStringExtra(ReminderScheduler.BODY).orEmpty()
        NotificationHelper.show(context, id.hashCode(), title, body)
        if (repeat == "daily" || repeat == "weekdays") {
            ReminderScheduler(context).schedule(ReminderEntity(id, time, repeat, title, body))
        }
    }
}
