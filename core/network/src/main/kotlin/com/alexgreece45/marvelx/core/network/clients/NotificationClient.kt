package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.notification.ResponseNotification
import com.alexgreece45.marvelx.core.network.model.api.notification.ResponseNotificationsUnreadCount
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult

interface NotificationClient {
    suspend fun getNotifications(page: Int): ApiResult<List<ResponseNotification>>
    suspend fun getUnreadCount(): ApiResult<ResponseNotificationsUnreadCount>
    suspend fun markAsRead(notificationId: String): ApiResult<String>
    suspend fun markAllAsRead(): ApiResult<String>
}
