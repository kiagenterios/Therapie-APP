package com.psych.therapieapp.data.repository

import com.psych.therapieapp.data.local.dao.ExerciseDao
import com.psych.therapieapp.data.local.entity.Exercise
import com.psych.therapieapp.data.local.entity.ExerciseCategory
import com.psych.therapieapp.data.seed.ExerciseSeeder
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val dao: ExerciseDao
) {

    fun getAll(): Flow<List<Exercise>> = dao.getAll()

    fun getByCategory(category: ExerciseCategory): Flow<List<Exercise>> =
        dao.getByCategory(category)

    suspend fun getById(id: Long): Exercise? = dao.getById(id)

    /**
     * Befuellt die DB einmalig mit den vordefinierten Uebungen.
     * Idempotent: laeuft nur, wenn noch keine Uebungen vorhanden sind.
     */
    suspend fun seedDatabase() {
        if (dao.count() > 0) return
        dao.insertAll(ExerciseSeeder.seedExercises())
    }
}
