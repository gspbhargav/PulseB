package com.pulseb.app.data.repository

import com.pulseb.app.data.db.ActivityEntry
import com.pulseb.app.data.db.ActivityEntryDao
import kotlinx.coroutines.flow.Flow

class ActivityRepository(
    private val dao: ActivityEntryDao
) {

    suspend fun insert(entry: ActivityEntry) {
        dao.insert(entry)
    }

    suspend fun update(entry: ActivityEntry) {
        dao.update(entry)
    }

    suspend fun delete(entry: ActivityEntry) {
        dao.delete(entry)
    }

    fun getAllEntries(): Flow<List<ActivityEntry>> {
        return dao.getAllEntries()
    }

    suspend fun getTopSuggestions(): List<ActivityEntry> {
        return dao.getTopSuggestions()
    }
}