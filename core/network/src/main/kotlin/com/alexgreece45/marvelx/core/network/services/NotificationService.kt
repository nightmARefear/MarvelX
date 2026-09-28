package com.alexgreece45.marvelx.core.network.services

import com.alexgreece45.marvelx.core.network.model.api.notification.ResponseNotification
import com.alexgreece45.marvelx.core.network.model.api.notification.ResponseNotificationsUnreadCount
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * The notifications inbox. Backend contract: `docs/backend-specs/notifications.md`.
 * The older friend-request `PATCH /notifications` stays in [ApiService].
 */
internal interface NotificationService {

    @GET("/api/private/marvelx/notifications")
    suspend fun getNotifications(@Query("page") page: Int): Response<List<ResponseNotification>>

    @GET("/api/private/marvelx/notifications/unread-count")
    suspend fun getUnreadCount(): Response<ResponseNotificationsUnreadCount>

    @PATCH("/api/private/marvelx/notifications/{notificationId}/read")
    suspend fun markAsRead(@Path("notificationId") notificationId: String): Response<Void>

    @PATCH("/api/private/marvelx/notifications/read-all")
    suspend fun markAllAsRead(): Response<Void>
}
