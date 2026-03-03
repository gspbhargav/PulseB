package com.pulseb.app.scheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.pulseb.app.service.LoggingForegroundService

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {

        Log.d("PulseB", "Alarm triggered")

        val serviceIntent = Intent(context, LoggingForegroundService::class.java)

        ContextCompat.startForegroundService(
            context,
            serviceIntent
        )
    }
}