package com.psych.therapieapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.psych.therapieapp.data.local.dao.ExerciseDao
import com.psych.therapieapp.data.local.dao.JournalDao
import com.psych.therapieapp.data.local.dao.JournalTagDao
import com.psych.therapieapp.data.local.dao.MoodDao
import com.psych.therapieapp.data.local.dao.ReminderDao
import com.psych.therapieapp.data.local.entity.Exercise
import com.psych.therapieapp.data.local.entity.JournalEntry
import com.psych.therapieapp.data.local.entity.JournalEntryTagCrossRef
import com.psych.therapieapp.data.local.entity.JournalTag
import com.psych.therapieapp.data.local.entity.MoodEntry
import com.psych.therapieapp.data.local.entity.Reminder

@Database(
    entities = [
        MoodEntry::class,
        JournalEntry::class,
        JournalTag::class,
        JournalEntryTagCrossRef::class,
        Exercise::class,
        Reminder::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun moodDao(): MoodDao
    abstract fun journalDao(): JournalDao
    abstract fun journalTagDao(): JournalTagDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        const val DATABASE_NAME = "therapieapp.db"
    }
}
