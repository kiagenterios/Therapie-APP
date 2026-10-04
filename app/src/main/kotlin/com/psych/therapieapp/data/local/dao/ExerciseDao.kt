package com.psych.therapieapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.psych.therapieapp.data.local.entity.Exercise
import com.psych.therapieapp.data.local.entity.ExerciseCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Query("SELECT * FROM exercises ORDER BY category ASC, title ASC")
    fun getAll(): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE category = :category ORDER BY title ASC")
    fun getByCategory(category: ExerciseCategory): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): Exercise?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(exercises: List<Exercise>): List<Long>

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun count(): Int
}
