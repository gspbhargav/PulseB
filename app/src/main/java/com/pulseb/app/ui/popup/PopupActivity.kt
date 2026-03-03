package com.pulseb.app.ui.popup

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.pulseb.app.data.db.ActivityEntry
import com.pulseb.app.data.db.AppDatabase
import kotlinx.coroutines.launch

class PopupActivity : ComponentActivity() {

    private var didLog = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show over lock screen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }

        setContent {
            PopupScreen(
                onLog = { text ->
                    logEntry(text)
                }
            )
        }
    }

    private fun logEntry(text: String) {
        lifecycleScope.launch {

            didLog = true

            val database = AppDatabase.getInstance(this@PopupActivity)

            database.activityEntryDao().insert(
                ActivityEntry(
                    timestamp = System.currentTimeMillis(),
                    durationMinutes = 15, // service will adjust later
                    activityText = text.trim().lowercase(),
                    wasMissed = false
                )
            )

            // Notify service that user logged
            sendBroadcast(
                Intent(ACTION_LOGGED)
            )

            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (!didLog) {
            // Notify service that user dismissed
            sendBroadcast(
                Intent(ACTION_MISSED)
            )
        }
    }

    companion object {
        const val ACTION_LOGGED = "com.pulseb.app.ACTION_LOGGED"
        const val ACTION_MISSED = "com.pulseb.app.ACTION_MISSED"
    }
}

@Composable
fun PopupScreen(
    onLog: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "What did you do in the last 15 minutes?",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Type activity...") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (text.isNotBlank()) {
                        onLog(text)
                    }
                }
            ) {
                Text("Log")
            }
        }
    }
}