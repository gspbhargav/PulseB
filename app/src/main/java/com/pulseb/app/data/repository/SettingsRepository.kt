package com.pulseb.app.data.repository

import com.pulseb.app.data.datastore.SettingsDataStore
import com.pulseb.app.data.datastore.SettingsPreferences
import kotlinx.coroutines.flow.Flow

class SettingsRepository(
    private val dataStore: SettingsDataStore
) {

    val settingsFlow: Flow<SettingsPreferences> =
        dataStore.settingsFlow

    suspend fun updateBaseInterval(value: Int) {
        dataStore.updateBaseInterval(value)
    }

    suspend fun updateCurrentInterval(value: Int) {
        dataStore.updateCurrentInterval(value)
    }
}