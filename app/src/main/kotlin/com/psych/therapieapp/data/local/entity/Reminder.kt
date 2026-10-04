package com.psych.therapieapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FeatureType {
    MOOD,
    JOURNAL,
    EXERCISE
}

/**
 * Reminder Entity.
 *
 * @param timeOfDayMinutes Zeit als Minuten ab Mitternacht (0..1439).
 *        Beispiel: 09:30 -> 9 * 60 + 30 = 570.
 * @param daysOfWeek Tage als kommaseparierter String, z.B. "1,2,3,4,5"
 *        Konvention: 1 = Montag ... 7 = Sonntag (ISO-8601 / java.time.DayOfWeek).
 */
@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "feature_type")
    val featureType: FeatureType,

    @ColumnInfo(name = "time_of_day_minutes")
    val timeOfDayMinutes: Int,

    @ColumnInfo(name = "days_of_week")
    val daysOfWeek: String,

    @ColumnInfo(name = "enabled")
    val enabled: Boolean = true
) {
    fun daysOfWeekAsSet(): Set<Int> =
        daysOfWeek.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it in 1..7 }
            .toSet()

    companion object {
        fun daysOfWeekToString(days: Set<Int>): String =
            days.filter { it in 1..7 }.sorted().joinToString(",")
    }
}
