package com.psych.therapieapp.ui.journal.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagChip(
    label: String,
    selected: Boolean = true,
    onRemove: (() -> Unit)? = null,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    InputChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) },
        trailingIcon = if (onRemove != null) {
            {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Tag entfernen",
                    modifier = Modifier
                        .size(InputChipDefaults.IconSize)
                        .clickable(onClick = onRemove)
                )
            }
        } else null,
        modifier = modifier
    )
}
