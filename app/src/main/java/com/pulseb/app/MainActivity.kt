package com.pulseb.app

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.pulseb.app.scheduler.AlarmScheduler
import com.pulseb.app.ui.home.HomeScreen
import com.pulseb.app.util.LogTags

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d(LogTags.APP, "MainActivity created")

        createNotificationChannel()

        setContent {
            HomeScreen()
        }
    }

    override fun onResume() {
        super.onResume()

        Log.d(LogTags.APP, "MainActivity onResume")

        val alarmManager = getSystemService(AlarmManager::class.java)

        if (alarmManager.canScheduleExactAlarms()) {
            Log.d(LogTags.APP, "Exact alarm permission granted → scheduling first alarm")
            AlarmScheduler.schedule(this, 5)
        } else {
            Log.d(LogTags.APP, "Exact alarm permission NOT granted")
        }
    }

    private fun createNotificationChannel() {

        val channel = NotificationChannel(
            "pulseb",
            "PulseB",
            NotificationManager.IMPORTANCE_LOW
        )

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }
}