package com.pulseb.app.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.pulseb.app.util.LogTags

object AlarmScheduler {

    fun schedule(context: Context, delaySeconds: Int) {

        Log.d(LogTags.APP, "Scheduling alarm in $delaySeconds seconds")

        val alarmManager =
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (!alarmManager.canScheduleExactAlarms()) {
            Log.d(LogTags.APP, "Cannot schedule exact alarms")
            return
        }

        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerTime = System.currentTimeMillis() + delaySeconds * 1000

        Log.d(LogTags.APP, "Alarm scheduled for $triggerTime")

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }
}