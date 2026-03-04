package com.pulseb.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.pulseb.app.scheduler.AlarmScheduler
import com.pulseb.app.ui.popup.PopupActivity
import com.pulseb.app.util.LogTags
import android.content.Context

class LoggingForegroundService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(LogTags.APP, "Foreground service started")

        startForeground(1, buildNotification())

        runLogic()

        return START_NOT_STICKY
    }

    private fun runLogic() {

        val prefs = getSharedPreferences("pulseb", Context.MODE_PRIVATE)

        val count = prefs.getInt("trigger_count", 0)

        Log.d(LogTags.APP, "Trigger count = $count")

        if (count >= 5) {
            Log.d(LogTags.APP, "Reached limit → stopping")
            prefs.edit().putInt("trigger_count", 0).apply()

            stopSelf()
            return
        }

        prefs.edit().putInt("trigger_count", count + 1).apply()

        launchPopup()

        Log.d(LogTags.APP, "Scheduling next alarm")

        AlarmScheduler.schedule(this, 15)

        stopSelf()
    }

    private fun buildNotification() =
        NotificationCompat.Builder(this, "pulseb")
            .setContentTitle("PulseB running")
            .setContentText("Tracking activity")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

    private fun launchPopup() {

        Log.d(LogTags.APP, "Launching popup")

        val intent = Intent(this, PopupActivity::class.java)

        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP
        )

        startActivity(intent)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}