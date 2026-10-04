package com.psych.therapieapp.data.repository

import com.psych.therapieapp.data.local.dao.ReminderDao
import com.psych.therapieapp.data.local.entity.Reminder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReminderRepository @Inject constructor(
    private val dao: ReminderDao
) {
    fun getAll(): Flow<List<Reminder>> = dao.getAll()

    fun getEnabled(): Flow<List<Reminder>> = dao.getEnabled()

    suspend fun getEnabledOnce(): List<Reminder> = dao.getEnabledOnce()

    suspend fun getById(id: Long): Reminder? = dao.getById(id)

    suspend fun insert(reminder: Reminder): Long = dao.insert(reminder)

    suspend fun update(reminder: Reminder) = dao.update(reminder)

    suspend fun delete(reminder: Reminder) = dao.delete(reminder)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun setEnabled(id: Long, enabled: Boolean) = dao.setEnabled(id, enabled)
}
