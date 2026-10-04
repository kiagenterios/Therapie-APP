package com.psych.therapieapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

/**
 * Ein Mood-Eintrag pro Tag. Date ist unique-indiziert, damit pro Datum
 * nur ein Eintrag existieren kann (Update statt zweiter Insert).
 */
@Entity(
    tableName = "mood_entries",
    indices = [Index(value = ["date"], unique = true)]
)
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0L,

    @ColumnInfo(name = "date")
    val date: LocalDate,

    /** 1 = sehr schlecht, 5 = sehr gut */
    @ColumnInfo(name = "mood_level")
    val moodLevel: Int,

    @ColumnInfo(name = "note")
    val note: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Instant = Instant.now()
)
