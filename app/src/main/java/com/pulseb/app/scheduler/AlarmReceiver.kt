package com.pulseb.app.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.pulseb.app.service.LoggingForegroundService
import com.pulseb.app.util.LogTags

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        Log.d(LogTags.APP, "AlarmReceiver triggered")

        val serviceIntent =
            Intent(context, LoggingForegroundService::class.java)

        ContextCompat.startForegroundService(context, serviceIntent)
    }
}