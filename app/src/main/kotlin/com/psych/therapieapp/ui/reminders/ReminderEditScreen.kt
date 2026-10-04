package com.psych.therapieapp.ui.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.psych.therapieapp.data.local.entity.FeatureType
import com.psych.therapieapp.data.local.entity.Reminder
import com.psych.therapieapp.service.rememberNotificationPermissionState
import com.psych.therapieapp.ui.reminders.components.DaySelector
import com.psych.therapieapp.ui.reminders.components.allDays
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderEditScreen(
    reminderId: Long?,
    onBack: () -> Unit,
    viewModel: RemindersViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val isEdit = reminderId != null && reminderId > 0L

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var featureType by remember { mutableStateOf(FeatureType.MOOD) }
    var selectedDays by remember { mutableStateOf(allDays()) }
    var enabled by remember { mutableStateOf(true) }
    var loaded by remember { mutableStateOf(!isEdit) }
    var showTimePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState(
        initialHour = 9,
        initialMinute = 0,
        is24Hour = true
    )

    val notificationPermission = rememberNotificationPermissionState()

    LaunchedEffect(reminderId) {
        if (isEdit && reminderId != null) {
            val r = viewModel.loadById(reminderId)
            if (r != null) {
                title = r.title
                message = r.message
                featureType = r.featureType
                selectedDays = r.daysOfWeekAsSet().ifEmpty { allDays() }
                enabled = r.enabled
                timePickerState.hour = (r.timeOfDayMinutes / 60).coerceIn(0, 23)
                timePickerState.minute = (r.timeOfDayMinutes % 60).coerceIn(0, 59)
            }
            loaded = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Erinnerung bearbeiten" else "Neue Erinnerung") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurueck")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (!notificationPermission.granted) {
                                notificationPermission.request()
                                return@IconButton
                            }
                            scope.launch {
                                val totalMinutes = timePickerState.hour * 60 + timePickerState.minute
                                if (isEdit && reminderId != null) {
                                    val existing = viewModel.loadById(reminderId)
                                    if (existing != null) {
                                        val updated = existing.copy(
                                            title = title.trim(),
                                            message = message.trim(),
                                            featureType = featureType,
                                            timeOfDayMinutes = totalMinutes,
                                            daysOfWeek = Reminder.daysOfWeekToString(selectedDays),
                                            enabled = enabled
                                        )
                                        viewModel.update(updated)
                                    }
                                } else {
                                    viewModel.add(
                                        title = title,
                                        message = message,
                                        featureType = featureType,
                                        timeOfDayMinutes = totalMinutes,
                                        daysOfWeek = selectedDays,
                                        enabled = enabled
                                    )
                                }
                                onBack()
                            }
                        },
                        enabled = title.isNotBlank() && loaded
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Speichern")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titel") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Nachricht") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            FeatureTypeDropdown(
                selected = featureType,
                onSelected = { featureType = it }
            )

            Column {
                Text(
                    text = "Uhrzeit",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = formatTime(timePickerState.hour * 60 + timePickerState.minute),
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { showTimePicker = true }) {
                        Text("Aendern")
                    }
                }
            }

            Column {
                Text(
                    text = "Wochentage",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                DaySelector(
                    selectedDays = selectedDays,
                    onSelectionChange = { selectedDays = it }
                )
            }

            if (!notificationPermission.granted) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Benachrichtigungen sind nicht erlaubt. Ohne diese Berechtigung kannst du keine Erinnerungen erhalten.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { notificationPermission.request() }) {
                        Text("Erlauben")
                    }
                }
            }
        }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Abbrechen")
                }
            },
            title = { Text("Uhrzeit waehlen") },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FeatureTypeDropdown(
    selected: FeatureType,
    onSelected: (FeatureType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = FeatureType.values().toList()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = labelFor(selected),
            onValueChange = {},
            readOnly = true,
            label = { Text("Feature") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        androidx.compose.material3.ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(labelFor(option)) },
                    onClick = {
                        onSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

private fun labelFor(type: FeatureType): String = when (type) {
    FeatureType.MOOD -> "Mood-Tracking"
    FeatureType.JOURNAL -> "Journal"
    FeatureType.EXERCISE -> "Uebungen"
}
