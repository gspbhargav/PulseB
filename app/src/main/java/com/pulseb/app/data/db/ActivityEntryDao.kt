package com.pulseb.app.data.db

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ActivityEntry)

    @Update
    suspend fun update(entry: ActivityEntry)

    @Delete
    suspend fun delete(entry: ActivityEntry)

    @Query("SELECT * FROM activity_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<ActivityEntry>>

    @Query("""
        SELECT * FROM activity_entries 
        WHERE activityText != 'untracked' AND wasMissed = 0
        GROUP BY activityText
        ORDER BY COUNT(*) DESC
        LIMIT 4
    """)
    suspend fun getTopSuggestions(): List<ActivityEntry>
}