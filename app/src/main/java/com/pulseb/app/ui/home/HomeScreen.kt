package com.pulseb.app.ui.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pulseb.app.permissions.PermissionRequests
import com.pulseb.app.permissions.PermissionState

@Composable
fun HomeScreen() {

    val context = LocalContext.current

    val hasNotification =
        PermissionState.hasNotificationPermission(context)

    val exactAlarm =
        PermissionState.canScheduleExactAlarms(context)

    val overlay =
        PermissionState.canDrawOverlays(context)

    val battery =
        PermissionState.isIgnoringBatteryOptimizations(context)
    Log.d("PulseB", "Ignoring battery optimizations: $battery")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        if (!overlay) {
            PermissionButton(
                text = "Enable Display Over Other Apps"
            ) {
                PermissionRequests.requestOverlay(context)
            }
        }

        if (!exactAlarm) {
            PermissionButton(
                text = "Enable Alarms & Reminders"
            ) {
                PermissionRequests.requestExactAlarm(context)
            }
        }

        if (!hasNotification) {
            PermissionButton(
                text = "Enable Notifications"
            ) {
                PermissionRequests.openNotificationSettings(context)
            }
        }

        if (!battery) {
            PermissionButton(
                text = "Disable Battery Optimization"
            ) {
                PermissionRequests.requestBatteryIgnore(context)
            }
        }

        if (overlay && exactAlarm && hasNotification && battery) {
            Text("All required permissions granted")
        }
    }
}

@Composable
private fun PermissionButton(
    text: String,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
    ) {
        Text(text)
    }
}