package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.common.extensions.minusHours
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.network.model.api.notification.ResponseNotification
import com.alexgreece45.marvelx.core.network.model.api.notification.ResponseNotificationDeepLinkAction
import com.alexgreece45.marvelx.core.network.model.api.notification.ResponseNotificationsUnreadCount
import com.alexgreece45.marvelx.core.network.model.generic.ApiResult
import kotlinx.coroutines.delay
import org.koin.core.annotation.Single
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

/**
 * An in-memory inbox. Read state survives reloads for the lifetime of the app process.
 */
@Single
internal class NotificationClientImpl : NotificationClient {

    private val notifications: List<ResponseNotification> = buildMockNotifications()
    private val readIds: MutableSet<String> = ConcurrentHashMap.newKeySet<String>().apply {
        // Everything older than the first few starts out read.
        addAll(notifications.drop(4).map { it.notificationId })
    }

    override suspend fun getNotifications(page: Int): ApiResult<List<ResponseNotification>> {
        delay(800.milliseconds)
        val pageItems = notifications
            .drop((page.coerceAtLeast(1) - 1) * PAGE_SIZE)
            .take(PAGE_SIZE)
            .map { it.copy(isRead = it.notificationId in readIds) }
        return ApiResult.Success(pageItems)
    }

    override suspend fun getUnreadCount(): ApiResult<ResponseNotificationsUnreadCount> =
        ApiResult.Success(ResponseNotificationsUnreadCount(count = notifications.count { it.notificationId !in readIds }))

    override suspend fun markAsRead(notificationId: String): ApiResult<String> {
        delay(300.milliseconds)
        if (notifications.none { it.notificationId == notificationId }) {
            return ApiResult.Failed("{\"error\":\"Notification not found.\"}")
        }
        readIds.add(notificationId)
        return ApiResult.Success("")
    }

    override suspend fun markAllAsRead(): ApiResult<String> {
        delay(300.milliseconds)
        readIds.addAll(notifications.map { it.notificationId })
        return ApiResult.Success("")
    }

    private companion object {
        const val PAGE_SIZE = 20
        const val NOTIFICATION_COUNT = 27

        fun buildMockNotifications(): List<ResponseNotification> {
            val now = DateUtil.getUTCInstant()
            return (0 until NOTIFICATION_COUNT).map { index ->
                val id = (NOTIFICATION_COUNT - index).toString()
                val orderId = (index / 2 + 1).toString()
                val orderItemId = (index + 1).toString()
                val createdAt = now.minusHours(index * 5L).toString()
                val imageUrl = "https://backend.alexandrosvoliotis.com/api/private/marvelx/user/${index % 4 + 1}/profile-image"

                if (index % 2 == 0) {
                    ResponseNotification(
                        notificationId = id,
                        channelType = "ListingPurchase",
                        title = "Your listing sold!",
                        body = "Someone bought \"Mock listing #$orderItemId\".",
                        imageUrl = imageUrl,
                        createdAt = createdAt,
                        isRead = false,
                        deepLinkAction = ResponseNotificationDeepLinkAction(
                            screenRoute = "OrderItemManagementRoute",
                            data = linkedMapOf("orderId" to orderId, "orderItemId" to orderItemId)
                        )
                    )
                } else {
                    ResponseNotification(
                        notificationId = id,
                        channelType = "OrderStatusChanged",
                        title = "Order #$orderId updated",
                        body = if (index % 4 == 1) "Some of your items have shipped." else "All your items have shipped.",
                        imageUrl = null,
                        createdAt = createdAt,
                        isRead = false,
                        deepLinkAction = ResponseNotificationDeepLinkAction(
                            screenRoute = "OrderDetailsRoute",
                            data = linkedMapOf("orderId" to orderId)
                        )
                    )
                }
            }
        }
    }
}
