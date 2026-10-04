package com.psych.therapieapp.ui.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psych.therapieapp.data.local.entity.FeatureType
import com.psych.therapieapp.data.local.entity.Reminder
import com.psych.therapieapp.data.repository.ReminderRepository
import com.psych.therapieapp.worker.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RemindersViewModel @Inject constructor(
    private val repository: ReminderRepository,
    private val scheduler: ReminderScheduler
) : ViewModel() {

    val reminders: StateFlow<List<Reminder>> = repository.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun add(
        title: String,
        message: String,
        featureType: FeatureType,
        timeOfDayMinutes: Int,
        daysOfWeek: Set<Int>,
        enabled: Boolean = true
    ) {
        viewModelScope.launch {
            val reminder = Reminder(
                title = title.trim(),
                message = message.trim(),
                featureType = featureType,
                timeOfDayMinutes = timeOfDayMinutes,
                daysOfWeek = Reminder.daysOfWeekToString(daysOfWeek),
                enabled = enabled
            )
            val id = repository.insert(reminder)
            val saved = reminder.copy(id = id)
            scheduler.schedule(saved)
        }
    }

    fun update(reminder: Reminder) {
        viewModelScope.launch {
            repository.update(reminder)
            scheduler.schedule(reminder)
        }
    }

    fun toggle(reminder: Reminder, enabled: Boolean) {
        viewModelScope.launch {
            val updated = reminder.copy(enabled = enabled)
            repository.update(updated)
            if (enabled) {
                scheduler.schedule(updated)
            } else {
                scheduler.cancel(updated.id)
            }
        }
    }

    fun delete(reminder: Reminder) {
        viewModelScope.launch {
            scheduler.cancel(reminder.id)
            repository.delete(reminder)
        }
    }

    suspend fun loadById(id: Long): Reminder? = repository.getById(id)
}
