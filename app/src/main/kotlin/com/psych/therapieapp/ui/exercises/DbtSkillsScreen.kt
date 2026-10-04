package com.psych.therapieapp.ui.exercises

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Statische DBT-Skill-Liste, unabhaengig von der DB.
 * Praktisch als Cheatsheet wenn schnell ein Skill nachgeschlagen wird.
 */

private data class DbtSkill(
    val acronym: String,
    val title: String,
    val items: List<Pair<String, String>>
)

private val staticDbtSkills = listOf(
    DbtSkill(
        acronym = "ACCEPTS",
        title = "Stresstoleranz - Ablenkung",
        items = listOf(
            "A" to "Aktivitaeten - Tu etwas Beschaeftigendes.",
            "C" to "Contributing - Hilf jemand anderem.",
            "C" to "Comparisons - Vergleiche dich (vorsichtig).",
            "E" to "Emotionen - Erzeuge ein anderes Gefuehl.",
            "P" to "Push away - Schieb das Problem mental beiseite.",
            "T" to "Thoughts - Beschaeftige den Kopf.",
            "S" to "Sensations - Starke Sinneseindruecke."
        )
    ),
    DbtSkill(
        acronym = "IMPROVE",
        title = "Den Moment verbessern",
        items = listOf(
            "I" to "Imagery - Stell dir einen sicheren Ort vor.",
            "M" to "Meaning - Such Sinn in der Situation.",
            "P" to "Prayer - Verbinde dich mit etwas Groesserem.",
            "R" to "Relaxation - Entspanne den Koerper.",
            "O" to "One thing - Konzentriere dich auf eine Sache.",
            "V" to "Vacation - Goenn dir eine kleine Auszeit.",
            "E" to "Encouragement - Sprich dir selbst Mut zu."
        )
    ),
    DbtSkill(
        acronym = "RADIATE",
        title = "Selbstberuhigung mit den 5 Sinnen",
        items = listOf(
            "Sehen" to "Etwas Schoenes anschauen (Foto, Pflanze).",
            "Hoeren" to "Beruhigende Musik oder Naturgeraeusche.",
            "Riechen" to "Tee, Lavendel, Kaffee.",
            "Schmecken" to "Achtsam etwas trinken/essen.",
            "Fuehlen" to "Decke, warme Tasse, weicher Stoff."
        )
    ),
    DbtSkill(
        acronym = "DEAR MAN",
        title = "Effektive Kommunikation",
        items = listOf(
            "D" to "Describe - Beschreibe sachlich die Situation.",
            "E" to "Express - Druecke deine Gefuehle/Meinung aus.",
            "A" to "Assert - Sag klar was du willst.",
            "R" to "Reinforce - Verstaerke positiv die Folge.",
            "M" to "Mindful - Bleib beim Thema.",
            "A" to "Appear confident - Tritt sicher auf.",
            "N" to "Negotiate - Sei verhandlungsbereit."
        )
    ),
    DbtSkill(
        acronym = "STOP",
        title = "Notfall-Skill bei hoher Anspannung",
        items = listOf(
            "S" to "Stop - Halte koerperlich inne.",
            "T" to "Take a step back - Tritt innerlich zurueck.",
            "O" to "Observe - Beobachte ohne zu bewerten.",
            "P" to "Proceed mindfully - Handle bewusst, nicht impulsiv."
        )
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DbtSkillsScreen(
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DBT-Skills") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Zurueck")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(staticDbtSkills, key = { it.acronym }) { skill ->
                DbtSkillCard(skill = skill)
            }
        }
    }
}

@Composable
private fun DbtSkillCard(skill: DbtSkill) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = skill.acronym.take(3),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.size(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = skill.acronym,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Text(
                        text = skill.title,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Einklappen" else "Ausklappen"
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    skill.items.forEach { (key, value) ->
                        Row(verticalAlignment = Alignment.Top) {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(width = 64.dp, height = 24.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = value,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
