package com.example.healthmanager.reminder

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.healthmanager.data.local.ReminderEntity

/** Persistent approximate-time fallback for devices without exact-alarm access. */
class ReminderWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        val id = inputData.getString("id") ?: return Result.failure()
        val time = inputData.getString("time") ?: "09:00"
        val repeat = inputData.getString("repeat") ?: "once"
        val title = inputData.getString("title") ?: "健康提醒"
        val body = inputData.getString("body") ?: ""
        NotificationHelper.show(applicationContext, id.hashCode(), title, body)
        if (!repeat.equals("once", ignoreCase = true)) {
            ReminderScheduler(applicationContext).schedule(
                ReminderEntity(id, time, repeat, title, body, enabled = true)
            )
        }
        return Result.success()
    }
}
