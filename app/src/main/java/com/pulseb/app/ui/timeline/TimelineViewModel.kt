package com.pulseb.app.ui.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pulseb.app.data.db.ActivityEntry
import com.pulseb.app.data.repository.ActivityRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TimelineViewModel(
    private val repository: ActivityRepository
) : ViewModel() {

    val entries: StateFlow<List<ActivityEntry>> =
        repository.getAllEntries()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun insertTestEntry() {
        viewModelScope.launch {
            repository.insert(
                ActivityEntry(
                    timestamp = System.currentTimeMillis(),
                    durationMinutes = 15,
                    activityText = "manual test",
                    wasMissed = false
                )
            )
        }
    }
}