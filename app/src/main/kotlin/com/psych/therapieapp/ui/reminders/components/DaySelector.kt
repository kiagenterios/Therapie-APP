package com.psych.therapieapp.ui.reminders.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * 7 Chips fuer Mo-So. Werte: 1=MO ... 7=SO.
 */
@Composable
fun DaySelector(
    selectedDays: Set<Int>,
    onSelectionChange: (Set<Int>) -> Unit,
    modifier: Modifier = Modifier
) {
    val days = listOf(
        1 to "Mo",
        2 to "Di",
        3 to "Mi",
        4 to "Do",
        5 to "Fr",
        6 to "Sa",
        7 to "So"
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        days.forEach { (value, label) ->
            val selected = value in selectedDays
            FilterChip(
                selected = selected,
                onClick = {
                    val newSet = if (selected) selectedDays - value else selectedDays + value
                    onSelectionChange(newSet)
                },
                label = { Text(label) },
                colors = FilterChipDefaults.filterChipColors()
            )
        }
    }
}

/**
 * Convenience: alle Tage selektiert (1..7).
 */
fun allDays(): Set<Int> = (1..7).toSet()

/**
 * Convenience: Werktage (Mo-Fr).
 */
fun weekdays(): Set<Int> = (1..5).toSet()
