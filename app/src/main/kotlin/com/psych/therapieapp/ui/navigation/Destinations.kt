package com.psych.therapieapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.MoodBad
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

sealed interface NavRoute

@Serializable
data object MoodGraph : NavRoute
@Serializable
data object MoodEntry : NavRoute
@Serializable
data object MoodHistory : NavRoute

@Serializable
data object JournalGraph : NavRoute
@Serializable
data object JournalList : NavRoute
@Serializable
data class JournalEdit(val entryId: Long = -1L) : NavRoute
@Serializable
data class JournalDetail(val entryId: Long) : NavRoute

@Serializable
data object ExercisesGraph : NavRoute
@Serializable
data object ExerciseList : NavRoute
@Serializable
data class ExerciseDetail(val exerciseId: Long) : NavRoute
@Serializable
data class BreathingScreen(val exerciseId: Long) : NavRoute
@Serializable
data object DbtSkills : NavRoute

@Serializable
data object RemindersGraph : NavRoute
@Serializable
data object RemindersList : NavRoute
@Serializable
data class ReminderEdit(val reminderId: Long = -1L) : NavRoute

enum class TopLevelDestination(
    val route: NavRoute,
    val icon: ImageVector,
    val label: String
) {
    Mood(MoodGraph, Icons.Outlined.MoodBad, "Mood"),
    Journal(JournalGraph, Icons.Outlined.Book, "Journal"),
    Exercises(ExercisesGraph, Icons.Outlined.SelfImprovement, "Übungen"),
    Reminders(RemindersGraph, Icons.Outlined.Notifications, "Erinnerungen"),
}
