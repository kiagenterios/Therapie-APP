package com.psych.therapieapp.ui.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psych.therapieapp.data.local.entity.JournalEntry
import com.psych.therapieapp.data.local.entity.JournalTag
import com.psych.therapieapp.data.local.relations.JournalEntryWithTags
import com.psych.therapieapp.data.repository.JournalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class JournalViewModel @Inject constructor(
    private val repository: JournalRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val entries: StateFlow<List<JournalEntryWithTags>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) repository.getAllEntries()
            else repository.searchEntries(query)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val allTags: StateFlow<List<JournalTag>> = repository.getAllTags()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun listAllEntries(): Flow<List<JournalEntryWithTags>> = repository.getAllEntries()

    fun searchEntries(query: String) {
        _searchQuery.value = query
    }

    fun getEntry(id: Long): Flow<JournalEntryWithTags?> = repository.getEntryById(id)

    fun addEntry(
        title: String,
        content: String,
        tagNames: List<String> = emptyList(),
        onComplete: (Long) -> Unit = {}
    ) {
        viewModelScope.launch {
            val entry = JournalEntry(
                title = title.trim(),
                content = content.trim(),
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            val newId = repository.insertEntry(entry)
            tagNames.map { it.trim() }.filter { it.isNotEmpty() }.distinct().forEach { name ->
                repository.addTagToEntry(newId, name)
            }
            onComplete(newId)
        }
    }

    fun updateEntry(
        entry: JournalEntry,
        tagNames: List<String> = emptyList(),
        onComplete: () -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.updateEntry(entry)
            repository.clearTagsForEntry(entry.id)
            tagNames.map { it.trim() }.filter { it.isNotEmpty() }.distinct().forEach { name ->
                repository.addTagToEntry(entry.id, name)
            }
            onComplete()
        }
    }

    fun deleteEntry(entry: JournalEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }

    fun deleteEntryById(id: Long) {
        viewModelScope.launch {
            repository.deleteEntryById(id)
        }
    }

    fun addTag(name: String, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val id = repository.insertTag(name)
            onComplete(id)
        }
    }

    fun addTagToEntry(entryId: Long, tagName: String) {
        viewModelScope.launch {
            repository.addTagToEntry(entryId, tagName)
        }
    }

    fun removeTagFromEntry(entryId: Long, tagId: Long) {
        viewModelScope.launch {
            repository.removeTagFromEntry(entryId, tagId)
        }
    }
}
