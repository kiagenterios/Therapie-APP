package com.psych.therapieapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.psych.therapieapp.ui.mood.MoodEntryScreen
import com.psych.therapieapp.ui.mood.MoodHistoryScreen
import com.psych.therapieapp.ui.journal.JournalListScreen
import com.psych.therapieapp.ui.journal.JournalEditScreen
import com.psych.therapieapp.ui.journal.JournalDetailScreen
import com.psych.therapieapp.ui.exercises.ExerciseListScreen
import com.psych.therapieapp.ui.exercises.ExerciseDetailScreen
import com.psych.therapieapp.ui.exercises.BreathingScreen as BreathingComposable
import com.psych.therapieapp.ui.exercises.DbtSkillsScreen
import com.psych.therapieapp.ui.reminders.RemindersScreen
import com.psych.therapieapp.ui.reminders.ReminderEditScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MoodGraph,
        modifier = modifier,
    ) {
        // MOOD
        navigation<MoodGraph>(startDestination = MoodEntry) {
            composable<MoodEntry> {
                MoodEntryScreen(
                    onShowHistory = { navController.navigate(MoodHistory) }
                )
            }
            composable<MoodHistory> {
                MoodHistoryScreen(onBack = { navController.popBackStack() })
            }
        }

        // JOURNAL
        navigation<JournalGraph>(startDestination = JournalList) {
            composable<JournalList> {
                JournalListScreen(
                    onAddNew = { navController.navigate(JournalEdit()) },
                    onOpenEntry = { id -> navController.navigate(JournalDetail(id)) },
                )
            }
            composable<JournalEdit> { backStackEntry ->
                val args: JournalEdit = backStackEntry.toRoute()
                JournalEditScreen(
                    entryId = args.entryId,
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                )
            }
            composable<JournalDetail> { backStackEntry ->
                val args: JournalDetail = backStackEntry.toRoute()
                JournalDetailScreen(
                    entryId = args.entryId,
                    onEdit = { navController.navigate(JournalEdit(args.entryId)) },
                    onBack = { navController.popBackStack() },
                )
            }
        }

        // EXERCISES
        navigation<ExercisesGraph>(startDestination = ExerciseList) {
            composable<ExerciseList> {
                ExerciseListScreen(
                    onOpenExercise = { id -> navController.navigate(ExerciseDetail(id)) },
                    onOpenDbtSkills = { navController.navigate(DbtSkills) },
                )
            }
            composable<ExerciseDetail> { backStackEntry ->
                val args: ExerciseDetail = backStackEntry.toRoute()
                ExerciseDetailScreen(
                    exerciseId = args.exerciseId,
                    onStartBreathing = { navController.navigate(BreathingScreen(args.exerciseId)) },
                    onBack = { navController.popBackStack() },
                )
            }
            composable<BreathingScreen> { backStackEntry ->
                val args: BreathingScreen = backStackEntry.toRoute()
                BreathingComposable(
                    exerciseId = args.exerciseId,
                    onBack = { navController.popBackStack() },
                )
            }
            composable<DbtSkills> {
                DbtSkillsScreen(onBack = { navController.popBackStack() })
            }
        }

        // REMINDERS
        navigation<RemindersGraph>(startDestination = RemindersList) {
            composable<RemindersList> {
                RemindersScreen(
                    onAddNew = { navController.navigate(ReminderEdit()) },
                    onEdit = { id -> navController.navigate(ReminderEdit(id)) },
                )
            }
            composable<ReminderEdit> { backStackEntry ->
                val args: ReminderEdit = backStackEntry.toRoute()
                ReminderEditScreen(
                    reminderId = args.reminderId,
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
