package com.example.healthmate.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.healthmate.data.local.ReminderEntity
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

class ReminderScheduler(private val context: Context) {
    fun schedule(item: ReminderEntity) {
        if (!item.enabled) { cancel(item.reminderId); return }
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(ID, item.reminderId); putExtra(TIME, item.time); putExtra(REPEAT, item.repeatType)
            putExtra(TITLE, item.title); putExtra(BODY, item.body)
        }
        val pending = PendingIntent.getBroadcast(context, item.reminderId.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val trigger = nextTime(item.time, item.repeatType).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && manager.canScheduleExactAlarms()) {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
            } else manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
        } catch (_: SecurityException) {
            manager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending)
        }
    }

    fun cancel(id: String) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val existing = PendingIntent.getBroadcast(context, id.hashCode(), intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
        if (existing != null) (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(existing)
    }

    private fun nextTime(value: String, repeat: String): LocalDateTime {
        val time = runCatching { LocalTime.parse(value) }.getOrDefault(LocalTime.of(20, 0))
        var result = LocalDateTime.now().withHour(time.hour).withMinute(time.minute).withSecond(0).withNano(0)
        if (!result.isAfter(LocalDateTime.now())) result = result.plusDays(1)
        if (repeat == "weekdays") while (result.dayOfWeek == DayOfWeek.SATURDAY || result.dayOfWeek == DayOfWeek.SUNDAY) result = result.plusDays(1)
        return result
    }

    companion object {
        const val ID = "reminder_id"; const val TIME = "reminder_time"; const val REPEAT = "reminder_repeat"
        const val TITLE = "reminder_title"; const val BODY = "reminder_body"
    }
}
