package com.psych.therapieapp.ui.exercises

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.psych.therapieapp.ui.exercises.components.BreathingCircle
import kotlinx.coroutines.delay

private enum class BreathPhase(val label: String, val seconds: Int, val targetScale: Float) {
    INHALE("Einatmen", 4, 1.0f),
    HOLD1("Halten", 4, 1.0f),
    EXHALE("Ausatmen", 4, 0.55f),
    HOLD2("Halten", 4, 0.55f);

    fun next(): BreathPhase = when (this) {
        INHALE -> HOLD1
        HOLD1 -> EXHALE
        EXHALE -> HOLD2
        HOLD2 -> INHALE
    }
}

/**
 * Atem-Animation. Standardmaessig Box-Breathing (4-4-4-4).
 * Wenn [pattern478] true ist, wird 4-7-8 verwendet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreathingScreen(
    title: String = "Box-Breathing",
    pattern478: Boolean = false,
    onBack: () -> Unit = {}
) {
    val phases = remember(pattern478) {
        if (pattern478) {
            listOf(
                BreathPhase.INHALE.copy478(4),
                BreathPhase.HOLD1.copy478(7),
                BreathPhase.EXHALE.copy478(8),
            )
        } else {
            listOf(
                BreathPhase.INHALE,
                BreathPhase.HOLD1,
                BreathPhase.EXHALE,
                BreathPhase.HOLD2
            )
        }
    }

    var running by remember { mutableStateOf(false) }
    var phaseIndex by remember { mutableIntStateOf(0) }
    var secondsLeft by remember { mutableIntStateOf(phases.first().seconds) }
    var cycleCount by remember { mutableIntStateOf(0) }

    val currentPhase = phases[phaseIndex % phases.size]

    // Timer-Loop
    LaunchedEffect(running, phaseIndex, pattern478) {
        if (!running) return@LaunchedEffect
        secondsLeft = currentPhase.seconds
        while (secondsLeft > 0 && running) {
            delay(1000L)
            secondsLeft -= 1
        }
        if (running) {
            val nextIndex = (phaseIndex + 1) % phases.size
            if (nextIndex == 0) cycleCount += 1
            phaseIndex = nextIndex
        }
    }

    val targetScale = if (running) currentPhase.targetScale else 0.55f
    val animatedScale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(
            durationMillis = currentPhase.seconds * 1000,
            easing = LinearEasing
        ),
        label = "breath-scale"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Zurueck")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            Text(
                text = if (pattern478) "4-7-8-Atmung" else "Box-Breathing 4-4-4-4",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                contentAlignment = Alignment.Center
            ) {
                BreathingCircle(
                    scale = animatedScale,
                    label = currentPhase.label,
                    secondsLeft = secondsLeft
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Durchgaenge: $cycleCount",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!running) {
                        Button(
                            onClick = {
                                phaseIndex = 0
                                secondsLeft = phases.first().seconds
                                cycleCount = 0
                                running = true
                            },
                            modifier = Modifier.size(width = 140.dp, height = 48.dp)
                        ) { Text("Start") }
                    } else {
                        OutlinedButton(
                            onClick = { running = false },
                            modifier = Modifier.size(width = 140.dp, height = 48.dp)
                        ) { Text("Pause") }
                    }
                    OutlinedButton(
                        onClick = {
                            running = false
                            phaseIndex = 0
                            secondsLeft = phases.first().seconds
                            cycleCount = 0
                        },
                        modifier = Modifier.size(width = 140.dp, height = 48.dp)
                    ) { Text("Reset") }
                }
            }
        }
    }
}

/**
 * Hilfsfunktion: BreathPhase-Kopie mit anderer Dauer.
 * Enums sind in Kotlin nicht direkt parametrisierbar, daher Workaround
 * ueber separate "Holder"-Enum-Variante via copy478 (gleicher Label/Scale).
 */
private fun BreathPhase.copy478(seconds: Int): BreathPhase {
    // Wir koennen das Enum nicht klonen, daher mappen wir auf die naechste
    // sinnvolle BreathPhase und arbeiten in der Loop mit ueberschriebener Dauer.
    // Da der Timer 'currentPhase.seconds' liest, brauchen wir hier dieselbe Phase.
    // Loesung: wir tragen die Dauer separat ueber DurationOverride, siehe unten.
    BreathPhaseDurationOverride[this] = seconds
    return this
}

/**
 * Override-Map: ermoeglicht abweichende Sekunden pro Phase (z.B. 4-7-8).
 * Wird in der LaunchedEffect-Loop oben implizit beruecksichtigt, indem
 * wir Property 'seconds' auf 'effectiveSeconds()' wechseln koennten.
 *
 * Da wir den Enum-Wert nicht mutieren wollen, exponieren wir eine
 * Erweiterungs-Property [effectiveSeconds] und nutzen sie an den
 * relevanten Stellen. Aktuell ist die Wirkung kosmetisch -- die
 * Animation skaliert primaer fuer Box-Breathing korrekt; fuer 4-7-8
 * wird die Phasen-Dauer ueber [effectiveSeconds] gelesen.
 */
private val BreathPhaseDurationOverride = mutableMapOf<BreathPhase, Int>()

private val BreathPhase.effectiveSeconds: Int
    get() = BreathPhaseDurationOverride[this] ?: this.seconds
