package com.example.healthmanager.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.healthmanager.data.local.ReminderEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/** Uses exact alarms only when system access is granted; otherwise uses WorkManager. */
@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun schedule(reminder: ReminderEntity) {
        if (!reminder.enabled) {
            cancel(reminder.reminderId)
            return
        }
        val triggerAt = nextTriggerMillis(reminder.time, reminder.repeatType)
        val delay = (triggerAt - System.currentTimeMillis()).coerceAtLeast(1L)
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val canUseExact = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || manager.canScheduleExactAlarms()

        if (canUseExact) {
            manager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent(reminder)
            )
        } else {
            val work = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(
                    Data.Builder()
                        .putString("id", reminder.reminderId)
                        .putString("time", reminder.time)
                        .putString("repeat", reminder.repeatType)
                        .putString("title", reminder.title)
                        .putString("body", reminder.body)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                workName(reminder.reminderId),
                ExistingWorkPolicy.REPLACE,
                work
            )
        }
    }

    fun cancel(id: String) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pending = PendingIntent.getBroadcast(
            context,
            id.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pending != null) manager.cancel(pending)
        WorkManager.getInstance(context).cancelUniqueWork(workName(id))
    }

    private fun pendingIntent(reminder: ReminderEntity): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("id", reminder.reminderId)
            putExtra("time", reminder.time)
            putExtra("repeat", reminder.repeatType)
            putExtra("title", reminder.title)
            putExtra("body", reminder.body)
        }
        return PendingIntent.getBroadcast(
            context,
            reminder.reminderId.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        fun workName(id: String) = "health-reminder-" + id

        fun nextTriggerMillis(time: String, repeatType: String): Long {
            val parsed = runCatching { LocalTime.parse(time) }.getOrDefault(LocalTime.of(9, 0))
            val now = LocalDateTime.now()
            var date = LocalDate.now()
            var candidate = LocalDateTime.of(date, parsed)
            if (!candidate.isAfter(now)) {
                date = date.plusDays(1)
                candidate = LocalDateTime.of(date, parsed)
            }
            if (repeatType.equals("weekdays", ignoreCase = true)) {
                while (date.dayOfWeek.value >= 6) {
                    date = date.plusDays(1)
                    candidate = LocalDateTime.of(date, parsed)
                }
            }
            return candidate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        }
    }
}
