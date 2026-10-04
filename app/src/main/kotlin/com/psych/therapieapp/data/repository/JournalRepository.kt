package com.psych.therapieapp.data.repository

import com.psych.therapieapp.data.local.dao.JournalDao
import com.psych.therapieapp.data.local.dao.JournalTagDao
import com.psych.therapieapp.data.local.entity.JournalEntry
import com.psych.therapieapp.data.local.entity.JournalEntryTagCrossRef
import com.psych.therapieapp.data.local.entity.JournalTag
import com.psych.therapieapp.data.local.relations.JournalEntryWithTags
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JournalRepository @Inject constructor(
    private val journalDao: JournalDao,
    private val tagDao: JournalTagDao
) {

    fun getAllEntries(): Flow<List<JournalEntryWithTags>> = journalDao.getAllEntries()

    fun getEntryById(id: Long): Flow<JournalEntryWithTags?> = journalDao.getEntryById(id)

    fun searchEntries(query: String): Flow<List<JournalEntryWithTags>> =
        journalDao.searchEntries(query)

    fun getAllTags(): Flow<List<JournalTag>> = tagDao.getAllTags()

    suspend fun insertEntry(entry: JournalEntry): Long = journalDao.insertEntry(entry)

    suspend fun updateEntry(entry: JournalEntry) {
        journalDao.updateEntry(entry.copy(updatedAt = LocalDateTime.now()))
    }

    suspend fun deleteEntry(entry: JournalEntry) = journalDao.deleteEntry(entry)

    suspend fun deleteEntryById(id: Long) = journalDao.deleteEntryById(id)

    suspend fun addTagToEntry(entryId: Long, tagName: String): Long {
        val trimmed = tagName.trim()
        if (trimmed.isEmpty()) return -1L
        val existing = tagDao.getTagByName(trimmed)
        val tagId = existing?.id ?: tagDao.insertTag(JournalTag(name = trimmed)).let {
            if (it == -1L) tagDao.getTagByName(trimmed)?.id ?: -1L else it
        }
        if (tagId > 0) {
            journalDao.insertEntryTagCrossRef(JournalEntryTagCrossRef(entryId, tagId))
        }
        return tagId
    }

    suspend fun removeTagFromEntry(entryId: Long, tagId: Long) {
        journalDao.deleteEntryTagCrossRef(JournalEntryTagCrossRef(entryId, tagId))
    }

    suspend fun clearTagsForEntry(entryId: Long) {
        journalDao.deleteAllCrossRefsForEntry(entryId)
    }

    suspend fun insertTag(name: String): Long {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return -1L
        val existing = tagDao.getTagByName(trimmed)
        return existing?.id ?: tagDao.insertTag(JournalTag(name = trimmed))
    }

    suspend fun deleteTag(tag: JournalTag) = tagDao.deleteTag(tag)
}
