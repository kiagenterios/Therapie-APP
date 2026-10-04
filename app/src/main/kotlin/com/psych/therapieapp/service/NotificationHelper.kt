package com.psych.therapieapp.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * NotificationHelper - Channel + Notification-Bau fuer Reminders.
 *
 * AndroidManifest.xml benoetigt:
 *   <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
 *   <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
 *   <!-- USE_EXACT_ALARM nur falls Exact-Alarm-Variante via AlarmManager genutzt wird.
 *        Hier nicht noetig, da wir ueber WorkManager (PeriodicWorkRequest) arbeiten. -->
 *   <!-- <uses-permission android:name="android.permission.USE_EXACT_ALARM" /> -->
 *
 * POST_NOTIFICATIONS (API 33+) muss zur Laufzeit angefragt werden -> siehe PermissionHelper.
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val CHANNEL_ID = "therapieapp_reminders"
        const val CHANNEL_NAME = "Erinnerungen"
        const val CHANNEL_DESCRIPTION = "Erinnerungen fuer Mood-Tracking, Journal und Uebungen."
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createChannel()
    }

    fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val existing = notificationManager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = CHANNEL_DESCRIPTION
                    enableLights(true)
                    enableVibration(true)
                }
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun buildNotification(
        title: String,
        message: String,
        pendingIntent: PendingIntent?,
        smallIconRes: Int = android.R.drawable.ic_dialog_info
    ): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(smallIconRes)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent)
        }
        return builder.build()
    }

    /**
     * Baut einen PendingIntent, der die App-MainActivity startet.
     * Verwendet FLAG_IMMUTABLE (Pflicht ab API 31).
     */
    fun buildOpenAppPendingIntent(reminderId: Long): PendingIntent? {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
            ?: return null
        launchIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        launchIntent.putExtra(EXTRA_REMINDER_ID, reminderId)

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        }
        return PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            launchIntent,
            flags
        )
    }

    fun notify(notificationId: Int, notification: Notification) {
        // Hinweis: Aufrufer sollte vorher pruefen, ob POST_NOTIFICATIONS gewaehrt ist (API 33+).
        notificationManager.notify(notificationId, notification)
    }

    @Suppress("unused")
    private fun appContext(): Context = ContextCompat.getContextForUser(context, android.os.Process.myUserHandle())
}
