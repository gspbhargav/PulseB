package com.pulseb.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_entries")
data class ActivityEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val durationMinutes: Int,
    val activityText: String,
    val wasMissed: Boolean
)