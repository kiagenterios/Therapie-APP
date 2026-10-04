package com.psych.therapieapp.ui.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.psych.therapieapp.data.local.entity.JournalEntry
import com.psych.therapieapp.ui.journal.components.TagChip
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JournalEditScreen(
    entryId: Long?,
    onSaved: () -> Unit,
    onBack: () -> Unit,
    viewModel: JournalViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var newTagInput by remember { mutableStateOf("") }
    val tags = remember { mutableStateListOf<String>() }
    var loadedEntry by remember { mutableStateOf<JournalEntry?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(entryId) {
        if (entryId != null && entryId > 0) {
            val data = viewModel.getEntry(entryId).firstOrNull()
            if (data != null) {
                loadedEntry = data.entry
                title = data.entry.title
                content = data.entry.content
                tags.clear()
                tags.addAll(data.tags.map { it.name })
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (entryId == null || entryId <= 0) "Neuer Eintrag" else "Eintrag bearbeiten") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zurueck")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            saveEntry(
                                viewModel = viewModel,
                                existing = loadedEntry,
                                title = title,
                                content = content,
                                tags = tags.toList(),
                                onComplete = onSaved
                            )
                        }
                    }) {
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
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Titel") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Inhalt") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("Tags", style = androidx.compose.material3.MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))

            if (tags.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tags.toList().forEach { name ->
                        TagChip(
                            label = name,
                            onRemove = { tags.remove(name) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            OutlinedTextField(
                value = newTagInput,
                onValueChange = { newTagInput = it },
                label = { Text("Neuer Tag") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    val trimmed = newTagInput.trim()
                    if (trimmed.isNotEmpty() && !tags.contains(trimmed)) {
                        tags.add(trimmed)
                    }
                    newTagInput = ""
                }
            ) {
                Text("Tag hinzufuegen")
            }
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = {
                    scope.launch {
                        saveEntry(
                            viewModel = viewModel,
                            existing = loadedEntry,
                            title = title,
                            content = content,
                            tags = tags.toList(),
                            onComplete = onSaved
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Speichern")
            }
        }
    }
}

private fun saveEntry(
    viewModel: JournalViewModel,
    existing: JournalEntry?,
    title: String,
    content: String,
    tags: List<String>,
    onComplete: () -> Unit
) {
    if (title.isBlank() && content.isBlank()) {
        onComplete()
        return
    }
    if (existing == null) {
        viewModel.addEntry(
            title = title,
            content = content,
            tagNames = tags
        ) { onComplete() }
    } else {
        val updated = existing.copy(
            title = title.trim(),
            content = content.trim(),
            updatedAt = LocalDateTime.now()
        )
        viewModel.updateEntry(
            entry = updated,
            tagNames = tags
        ) { onComplete() }
    }
}
