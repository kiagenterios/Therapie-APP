package com.psych.therapieapp.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.psych.therapieapp.data.local.entity.Reminder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Schedule/Cancel von Reminder-WorkRequests.
 *
 * PeriodicWorkRequest hat Mindest-Intervall = 15 Minuten. Wir nutzen 24h Intervall,
 * setzen den initialDelay so, dass der Worker zur Zielzeit feuert. Der Worker selbst
 * prueft den Wochentag und unterdrueckt Notifications an inaktiven Tagen.
 */
@Singleton
class ReminderScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val TAG_PREFIX = "reminder_"
        const val UNIQUE_PREFIX = "reminder_unique_"
        val INTERVAL: Duration = Duration.ofDays(1)
    }

    private val workManager: WorkManager get() = WorkManager.getInstance(context)

    fun tagFor(id: Long): String = "$TAG_PREFIX$id"
    fun uniqueNameFor(id: Long): String = "$UNIQUE_PREFIX$id"

    /**
     * Plant den Reminder als taeglich wiederkehrenden Worker.
     * Falls bereits geplant, wird der bestehende ersetzt.
     */
    fun schedule(reminder: Reminder) {
        if (!reminder.enabled) {
            cancel(reminder.id)
            return
        }

        val initialDelayMillis = computeInitialDelayMillis(reminder.timeOfDayMinutes)

        val data = Data.Builder()
            .putLong(ReminderWorker.KEY_REMINDER_ID, reminder.id)
            .build()

        val request = PeriodicWorkRequestBuilder<ReminderWorker>(INTERVAL)
            .setInitialDelay(Duration.ofMillis(initialDelayMillis))
            .setInputData(data)
            .addTag(tagFor(reminder.id))
            .build()

        workManager.enqueueUniquePeriodicWork(
            uniqueNameFor(reminder.id),
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun cancel(id: Long) {
        workManager.cancelUniqueWork(uniqueNameFor(id))
        workManager.cancelAllWorkByTag(tagFor(id))
    }

    fun cancelAll() {
        workManager.cancelAllWorkByTag(TAG_PREFIX.trimEnd('_'))
    }

    /**
     * Liefert die Millisekunden bis zur naechsten Zielzeit (heute oder morgen).
     */
    internal fun computeInitialDelayMillis(
        timeOfDayMinutes: Int,
        now: LocalDateTime = LocalDateTime.now()
    ): Long {
        val safeMinutes = timeOfDayMinutes.coerceIn(0, 24 * 60 - 1)
        val targetTime = LocalTime.of(safeMinutes / 60, safeMinutes % 60)
        var targetDateTime = now.toLocalDate().atTime(targetTime)
        if (!targetDateTime.isAfter(now)) {
            targetDateTime = targetDateTime.plusDays(1)
        }
        return Duration.between(now, targetDateTime).toMillis().coerceAtLeast(0L)
    }
}
