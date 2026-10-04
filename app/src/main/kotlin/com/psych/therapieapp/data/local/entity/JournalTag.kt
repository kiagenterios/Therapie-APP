package com.psych.therapieapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "journal_tags",
    indices = [Index(value = ["name"], unique = true)]
)
data class JournalTag(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String
)
