package com.dzadafa.mywallet

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import java.util.Calendar

object ReminderScheduler {

    const val PREFS_NAME = "ReminderPrefs"
    const val KEY_REMINDER_ENABLED = "reminder_enabled"
    const val KEY_REMINDER_HOUR = "reminder_hour"
    const val KEY_REMINDER_MINUTE = "reminder_minute"
    
    private const val REMINDER_REQUEST_CODE = 123

    fun scheduleReminder(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (!prefs.getBoolean(KEY_REMINDER_ENABLED, false)) {
            return
        }

        val hour = prefs.getInt(KEY_REMINDER_HOUR, 21)
        val minute = prefs.getInt(KEY_REMINDER_MINUTE, 0)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun saveReminderSetting(context: Context, enabled: Boolean, hour: Int, minute: Int) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean(KEY_REMINDER_ENABLED, enabled)
            .putInt(KEY_REMINDER_HOUR, hour)
            .putInt(KEY_REMINDER_MINUTE, minute)
            .apply()

        if (enabled) {
            scheduleReminder(context)
        } else {
            cancelReminder(context)
        }
    }

    fun getReminderTime(context: Context): Pair<Int, Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val hour = prefs.getInt(KEY_REMINDER_HOUR, 21)
        val minute = prefs.getInt(KEY_REMINDER_MINUTE, 0)
        return Pair(hour, minute)
    }
    
    fun getIsReminderEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_REMINDER_ENABLED, false)
    }
}
