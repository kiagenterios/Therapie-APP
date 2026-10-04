package com.psych.therapieapp.ui.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Mood
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.psych.therapieapp.data.local.entity.FeatureType
import com.psych.therapieapp.data.local.entity.Reminder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    onAddReminder: () -> Unit,
    onEditReminder: (Long) -> Unit,
    viewModel: RemindersViewModel = hiltViewModel()
) {
    val reminders by viewModel.reminders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Erinnerungen") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddReminder) {
                Icon(Icons.Default.Add, contentDescription = "Neue Erinnerung")
            }
        }
    ) { padding ->
        if (reminders.isEmpty()) {
            EmptyState(padding)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = reminders, key = { it.id }) { reminder ->
                    ReminderCard(
                        reminder = reminder,
                        onClick = { onEditReminder(reminder.id) },
                        onToggle = { enabled -> viewModel.toggle(reminder, enabled) },
                        onDelete = { viewModel.delete(reminder) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.size(12.dp))
            Text(
                "Noch keine Erinnerungen.",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.size(4.dp))
            Text(
                "Tippe auf +, um deine erste Erinnerung anzulegen.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ReminderCard(
    reminder: Reminder,
    onClick: () -> Unit,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = iconFor(reminder.featureType),
                contentDescription = reminder.featureType.name,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = reminder.title.ifBlank { "(Ohne Titel)" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${formatTime(reminder.timeOfDayMinutes)}  •  ${formatDays(reminder.daysOfWeekAsSet())}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (reminder.message.isNotBlank()) {
                    Text(
                        text = reminder.message,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 2
                    )
                }
            }
            Switch(
                checked = reminder.enabled,
                onCheckedChange = onToggle
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Loeschen")
            }
        }
    }
}

private fun iconFor(type: FeatureType): ImageVector = when (type) {
    FeatureType.MOOD -> Icons.Default.Mood
    FeatureType.JOURNAL -> Icons.Default.Book
    FeatureType.EXERCISE -> Icons.Default.FitnessCenter
}

internal fun formatTime(minutes: Int): String {
    val safe = minutes.coerceIn(0, 24 * 60 - 1)
    val h = safe / 60
    val m = safe % 60
    return "%02d:%02d".format(h, m)
}

internal fun formatDays(days: Set<Int>): String {
    if (days.isEmpty()) return "Keine Tage"
    if (days == (1..7).toSet()) return "Taeglich"
    if (days == (1..5).toSet()) return "Mo-Fr"
    if (days == setOf(6, 7)) return "Wochenende"
    val labels = mapOf(
        1 to "Mo", 2 to "Di", 3 to "Mi", 4 to "Do",
        5 to "Fr", 6 to "Sa", 7 to "So"
    )
    return days.sorted().joinToString(", ") { labels[it] ?: it.toString() }
}
