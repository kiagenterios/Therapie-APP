package com.psych.therapieapp.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.psych.therapieapp.data.repository.ReminderRepository
import com.psych.therapieapp.service.NotificationHelper
import com.psych.therapieapp.service.PermissionHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * Worker, der eine Reminder-Notification posted.
 *
 * Input-Data:
 *  - KEY_REMINDER_ID: Long  (Reminder-ID in Room)
 */
@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: ReminderRepository,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, params) {

    companion object {
        const val KEY_REMINDER_ID = "key_reminder_id"
    }

    override suspend fun doWork(): Result {
        val reminderId = inputData.getLong(KEY_REMINDER_ID, -1L)
        if (reminderId <= 0L) return Result.failure()

        val reminder = repository.getById(reminderId) ?: return Result.success()
        if (!reminder.enabled) return Result.success()

        // Prueft Wochentag: nur an aktivierten Tagen feuern.
        val today = java.time.LocalDate.now().dayOfWeek.value // 1=MO ... 7=SO
        val days = reminder.daysOfWeekAsSet()
        if (days.isNotEmpty() && today !in days) {
            return Result.success()
        }

        if (!PermissionHelper.hasNotificationPermission(applicationContext)) {
            return Result.success()
        }

        val pendingIntent = notificationHelper.buildOpenAppPendingIntent(reminder.id)
        val notification = notificationHelper.buildNotification(
            title = reminder.title,
            message = reminder.message,
            pendingIntent = pendingIntent
        )
        val notificationId = reminder.id.toInt().takeIf { it > 0 } ?: reminder.id.hashCode()
        notificationHelper.notify(notificationId, notification)

        return Result.success()
    }
}
