package com.psych.therapieapp.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

/**
 * Hilfen zur POST_NOTIFICATIONS-Berechtigung (API 33+).
 *
 * Auf API < 33 ist die Erlaubnis implizit -> wir geben true zurueck.
 */
object PermissionHelper {

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }
}

/**
 * Compose-Helper: fordert POST_NOTIFICATIONS an, falls noetig.
 *
 * Usage:
 * ```
 * val state = rememberNotificationPermissionState()
 * if (!state.granted) {
 *     Button(onClick = { state.request() }) { Text("Benachrichtigungen erlauben") }
 * }
 * ```
 */
class NotificationPermissionState(
    val granted: Boolean,
    val request: () -> Unit
)

@Composable
fun rememberNotificationPermissionState(
    onResult: (Boolean) -> Unit = {}
): NotificationPermissionState {
    val context = LocalContext.current
    var granted by remember { mutableStateOf(PermissionHelper.hasNotificationPermission(context)) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        granted = isGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        onResult(granted)
    }

    LaunchedEffect(Unit) {
        granted = PermissionHelper.hasNotificationPermission(context)
    }

    val request: () -> Unit = {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            granted = true
            onResult(true)
        }
    }

    return NotificationPermissionState(granted = granted, request = request)
}
