package com.psych.therapieapp.di

import android.content.Context
import androidx.room.Room
import com.psych.therapieapp.data.local.AppDatabase
import com.psych.therapieapp.data.local.dao.ExerciseDao
import com.psych.therapieapp.data.local.dao.JournalDao
import com.psych.therapieapp.data.local.dao.JournalTagDao
import com.psych.therapieapp.data.local.dao.MoodDao
import com.psych.therapieapp.data.local.dao.ReminderDao
import com.psych.therapieapp.data.seed.ExerciseSeeder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration(true)
            .addCallback(object : androidx.room.RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    applicationScope.launch {
                        ExerciseSeeder.seedIfEmpty(context)
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideMoodDao(db: AppDatabase): MoodDao = db.moodDao()

    @Provides
    fun provideJournalDao(db: AppDatabase): JournalDao = db.journalDao()

    @Provides
    fun provideJournalTagDao(db: AppDatabase): JournalTagDao = db.journalTagDao()

    @Provides
    fun provideExerciseDao(db: AppDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideReminderDao(db: AppDatabase): ReminderDao = db.reminderDao()
}
