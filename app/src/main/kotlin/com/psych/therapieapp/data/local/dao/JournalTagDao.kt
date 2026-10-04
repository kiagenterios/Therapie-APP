package com.psych.therapieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.psych.therapieapp.data.local.entity.JournalTag
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalTagDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTag(tag: JournalTag): Long

    @Delete
    suspend fun deleteTag(tag: JournalTag)

    @Query("DELETE FROM journal_tags WHERE id = :id")
    suspend fun deleteTagById(id: Long)

    @Query("SELECT * FROM journal_tags ORDER BY name ASC")
    fun getAllTags(): Flow<List<JournalTag>>

    @Query("SELECT * FROM journal_tags WHERE name = :name LIMIT 1")
    suspend fun getTagByName(name: String): JournalTag?

    @Query("SELECT * FROM journal_tags WHERE id = :id LIMIT 1")
    suspend fun getTagById(id: Long): JournalTag?
}
