package com.pulseb.app.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "pulseb_settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val BASE_INTERVAL = intPreferencesKey("base_interval")
        val CURRENT_INTERVAL = intPreferencesKey("current_interval")
        val START_HOUR = intPreferencesKey("start_hour")
        val END_HOUR = intPreferencesKey("end_hour")
        val POPUP_DURATION = intPreferencesKey("popup_duration")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
    }

    val settingsFlow: Flow<SettingsPreferences> =
        context.dataStore.data.map { prefs ->
            SettingsPreferences(
                baseInterval = prefs[BASE_INTERVAL] ?: 15,
                currentInterval = prefs[CURRENT_INTERVAL] ?: 15,
                startHour = prefs[START_HOUR] ?: 8,
                endHour = prefs[END_HOUR] ?: 22,
                popupDurationSeconds = prefs[POPUP_DURATION] ?: 45,
                soundEnabled = prefs[SOUND_ENABLED] ?: true,
                vibrationEnabled = prefs[VIBRATION_ENABLED] ?: true
            )
        }

    suspend fun updateBaseInterval(value: Int) {
        context.dataStore.edit {
            it[BASE_INTERVAL] = value
        }
    }

    suspend fun updateCurrentInterval(value: Int) {
        context.dataStore.edit {
            it[CURRENT_INTERVAL] = value
        }
    }
}

data class SettingsPreferences(
    val baseInterval: Int,
    val currentInterval: Int,
    val startHour: Int,
    val endHour: Int,
    val popupDurationSeconds: Int,
    val soundEnabled: Boolean,
    val vibrationEnabled: Boolean
)