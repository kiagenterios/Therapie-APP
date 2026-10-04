package com.psych.therapieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.psych.therapieapp.data.local.entity.MoodEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface MoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: MoodEntry): Long

    @Update
    suspend fun update(entry: MoodEntry)

    @Delete
    suspend fun delete(entry: MoodEntry)

    @Query("SELECT * FROM mood_entries WHERE date = :date LIMIT 1")
    suspend fun getByDate(date: LocalDate): MoodEntry?

    @Query("SELECT * FROM mood_entries WHERE date = :date LIMIT 1")
    fun observeByDate(date: LocalDate): Flow<MoodEntry?>

    @Query("SELECT * FROM mood_entries ORDER BY date DESC")
    fun getAll(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE date >= :from ORDER BY date ASC")
    fun getLast30Days(from: LocalDate): Flow<List<MoodEntry>>
}
