package com.psych.therapieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.psych.therapieapp.data.local.entity.JournalEntry
import com.psych.therapieapp.data.local.entity.JournalEntryTagCrossRef
import com.psych.therapieapp.data.local.relations.JournalEntryWithTags
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: JournalEntry): Long

    @Update
    suspend fun updateEntry(entry: JournalEntry)

    @Delete
    suspend fun deleteEntry(entry: JournalEntry)

    @Query("DELETE FROM journal_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Long)

    @Transaction
    @Query("SELECT * FROM journal_entries ORDER BY updatedAt DESC")
    fun getAllEntries(): Flow<List<JournalEntryWithTags>>

    @Transaction
    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    fun getEntryById(id: Long): Flow<JournalEntryWithTags?>

    @Transaction
    @Query(
        """
        SELECT * FROM journal_entries
        WHERE title LIKE '%' || :query || '%'
           OR content LIKE '%' || :query || '%'
        ORDER BY updatedAt DESC
        """
    )
    fun searchEntries(query: String): Flow<List<JournalEntryWithTags>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEntryTagCrossRef(crossRef: JournalEntryTagCrossRef)

    @Delete
    suspend fun deleteEntryTagCrossRef(crossRef: JournalEntryTagCrossRef)

    @Query("DELETE FROM journal_entry_tag_cross_ref WHERE entryId = :entryId")
    suspend fun deleteAllCrossRefsForEntry(entryId: Long)
}
