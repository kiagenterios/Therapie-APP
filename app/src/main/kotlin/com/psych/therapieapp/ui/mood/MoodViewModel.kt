package com.psych.therapieapp.ui.mood

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psych.therapieapp.data.local.entity.MoodEntry
import com.psych.therapieapp.data.repository.MoodRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MoodViewModel @Inject constructor(
    private val repository: MoodRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoodUiState())
    val uiState: StateFlow<MoodUiState> = _uiState.asStateFlow()

    init {
        observeEntries()
        loadForDate(LocalDate.now())
    }

    private fun observeEntries() {
        viewModelScope.launch {
            combine(
                repository.observeAll(),
                repository.observeLast30Days()
            ) { all, recent ->
                all to recent
            }.collect { (all, recent) ->
                _uiState.update {
                    it.copy(allEntries = all, recentEntries = recent)
                }
            }
        }
    }

    fun selectDate(date: LocalDate) {
        _uiState.update { it.copy(selectedDate = date, saved = false) }
        loadForDate(date)
    }

    private fun loadForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val existing = repository.getByDate(date)
            _uiState.update {
                if (existing != null) {
                    it.copy(
                        isLoading = false,
                        existingEntryId = existing.id,
                        moodLevel = existing.moodLevel,
                        note = existing.note.orEmpty()
                    )
                } else {
                    it.copy(
                        isLoading = false,
                        existingEntryId = null,
                        moodLevel = 3,
                        note = ""
                    )
                }
            }
        }
    }

    fun setMoodLevel(level: Int) {
        if (level !in 1..5) return
        _uiState.update { it.copy(moodLevel = level, saved = false) }
    }

    fun setNote(note: String) {
        _uiState.update { it.copy(note = note, saved = false) }
    }

    fun addEntry() {
        val state = _uiState.value
        if (!state.canSave) return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                repository.upsert(
                    date = state.selectedDate,
                    moodLevel = state.moodLevel,
                    note = state.note
                )
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, saved = true) }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "Speichern fehlgeschlagen")
                }
            }
        }
    }

    /** Update eines bestehenden Eintrags (z.B. aus History). */
    fun updateEntry(entry: MoodEntry) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                repository.update(entry)
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false, saved = true) }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = e.message ?: "Update fehlgeschlagen")
                }
            }
        }
    }

    fun deleteEntry(entry: MoodEntry) {
        viewModelScope.launch {
            runCatching { repository.delete(entry) }
                .onFailure { e ->
                    _uiState.update { it.copy(errorMessage = e.message ?: "Loeschen fehlgeschlagen") }
                }
        }
    }

    fun consumeSaved() {
        _uiState.update { it.copy(saved = false) }
    }

    fun consumeError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
