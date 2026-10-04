package com.psych.therapieapp.ui.mood

import com.psych.therapieapp.data.local.entity.MoodEntry
import java.time.LocalDate

/**
 * UI-State fuer Mood-Entry-Screen und History-Screen.
 */
data class MoodUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val moodLevel: Int = 3,
    val note: String = "",
    val existingEntryId: Long? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val errorMessage: String? = null,

    /** Letzte 30 Tage fuer Diagramm + Liste */
    val recentEntries: List<MoodEntry> = emptyList(),
    val allEntries: List<MoodEntry> = emptyList()
) {
    val isEditing: Boolean get() = existingEntryId != null
    val canSave: Boolean get() = moodLevel in 1..5 && !isSaving
}
