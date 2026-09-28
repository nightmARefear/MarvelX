package com.alexgreece45.marvelx.ui.screens.notifications

internal object NotificationsTestTags {
    // NotificationsContract.Event.OnBackClicked
    const val BACK_BUTTON = "notifications_back_button"

    // NotificationsContract.Event.OnMarkAllAsReadClicked
    const val MARK_ALL_AS_READ_BUTTON = "notifications_mark_all_as_read_button"

    // NotificationsContract.Event.OnErrorRetryClicked
    const val ERROR_RETRY_BUTTON = "notifications_error_retry_button"

    // NotificationsContract.Event.OnNotificationClicked (suffixed with the notification id)
    const val NOTIFICATION_ROW = "notifications_row"

    fun notificationRow(notificationId: String): String = "${NOTIFICATION_ROW}_$notificationId"
}
