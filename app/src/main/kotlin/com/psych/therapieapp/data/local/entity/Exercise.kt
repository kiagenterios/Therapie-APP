package com.psych.therapieapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExerciseCategory {
    BREATHING,
    CBT,
    DBT,
    MINDFULNESS
}

/**
 * Eine gefuehrte Uebung (Atmung / CBT / DBT-Skill / Achtsamkeit).
 *
 * [steps] ist eine JSON-serialisierte Liste von Strings, damit Room
 * keine weiteren TypeConverter braucht. Die Liste wird im Repository
 * per [com.psych.therapieapp.data.local.Converters] konvertiert.
 */
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "category")
    val category: ExerciseCategory,

    @ColumnInfo(name = "description")
    val description: String,

    /** JSON-Liste der einzelnen Schritte (deutsche Texte). */
    @ColumnInfo(name = "steps")
    val steps: String,

    @ColumnInfo(name = "duration_minutes")
    val durationMinutes: Int
)
