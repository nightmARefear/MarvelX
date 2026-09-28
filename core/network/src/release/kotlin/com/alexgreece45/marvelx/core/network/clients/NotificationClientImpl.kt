package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.notification.ResponseNotification
import com.alexgreece45.marvelx.core.network.model.api.notification.ResponseNotificationsUnreadCount
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import com.alexgreece45.marvelx.core.network.services.NotificationService
import com.alexgreece45.marvelx.core.network.util.executeApiServiceCall
import com.alexgreece45.marvelx.core.network.util.executeApiServiceCallNoTransformation
import org.koin.core.annotation.Single

@Single
internal class NotificationClientImpl(
    private val notificationService: NotificationService
) : NotificationClient {

    override suspend fun getNotifications(page: Int): ApiResult<List<ResponseNotification>> {
        return executeApiServiceCall { notificationService.getNotifications(page) }
    }

    override suspend fun getUnreadCount(): ApiResult<ResponseNotificationsUnreadCount> {
        return executeApiServiceCall { notificationService.getUnreadCount() }
    }

    override suspend fun markAsRead(notificationId: String): ApiResult<String> {
        return executeApiServiceCallNoTransformation { notificationService.markAsRead(notificationId) }
    }

    override suspend fun markAllAsRead(): ApiResult<String> {
        return executeApiServiceCallNoTransformation { notificationService.markAllAsRead() }
    }
}
