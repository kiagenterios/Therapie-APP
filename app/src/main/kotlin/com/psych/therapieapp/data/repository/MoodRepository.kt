package com.psych.therapieapp.data.repository

import com.psych.therapieapp.data.local.dao.MoodDao
import com.psych.therapieapp.data.local.entity.MoodEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MoodRepository @Inject constructor(
    private val moodDao: MoodDao
) {

    fun observeAll(): Flow<List<MoodEntry>> = moodDao.getAll()

    fun observeLast30Days(today: LocalDate = LocalDate.now()): Flow<List<MoodEntry>> =
        moodDao.getLast30Days(today.minusDays(29))

    fun observeByDate(date: LocalDate): Flow<MoodEntry?> =
        moodDao.observeByDate(date)

    suspend fun getByDate(date: LocalDate): MoodEntry? =
        moodDao.getByDate(date)

    suspend fun upsert(date: LocalDate, moodLevel: Int, note: String?): Long {
        require(moodLevel in 1..5) { "moodLevel must be between 1 and 5" }
        val existing = moodDao.getByDate(date)
        return if (existing == null) {
            moodDao.insert(
                MoodEntry(
                    date = date,
                    moodLevel = moodLevel,
                    note = note?.ifBlank { null }
                )
            )
        } else {
            val updated = existing.copy(
                moodLevel = moodLevel,
                note = note?.ifBlank { null }
            )
            moodDao.update(updated)
            updated.id
        }
    }

    suspend fun update(entry: MoodEntry) = moodDao.update(entry)

    suspend fun delete(entry: MoodEntry) = moodDao.delete(entry)
}
