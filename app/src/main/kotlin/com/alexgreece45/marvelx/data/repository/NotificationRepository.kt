package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoFlow
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.clients.NotificationClient
import com.alexgreece45.marvelx.data.model.notification.NotificationItemData
import com.alexgreece45.marvelx.data.model.notification.toData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface NotificationRepository {

    /** Unread inbox notifications, for the notifications bell badge. */
    val unreadCount: StateFlow<Int>

    suspend fun markIncomingFriendRequestAsRead(): Boolean

    suspend fun getNotifications(page: Int): RepositoryResult<List<NotificationItemData>>
    suspend fun refreshUnreadCount()
    suspend fun markAsRead(notificationId: String): Boolean
    suspend fun markAllAsRead(): Boolean
}

@Single
class NotificationRepositoryImpl(
    private val apiClient: ApiClient,
    private val notificationClient: NotificationClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : NotificationRepository {

    private val _unreadCount = MutableStateFlow(0)
    override val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    override suspend fun markIncomingFriendRequestAsRead(): Boolean {
        return handleApiResultNoTransformationNoFlow(
            apiCall = apiClient::markNotificationAsOpened,
            ioDispatcher = ioDispatcher
        ) is RepositoryResult.Success
    }

    override suspend fun getNotifications(page: Int): RepositoryResult<List<NotificationItemData>> {
        return handleApiResultNoFlow(
            apiCall = { notificationClient.getNotifications(page) },
            transformation = { this.map { it.toData() } },
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun refreshUnreadCount() {
        val result = handleApiResultNoFlow(
            apiCall = notificationClient::getUnreadCount,
            transformation = { count },
            ioDispatcher = ioDispatcher
        )
        if (result is RepositoryResult.Success) _unreadCount.value = result.data
    }

    override suspend fun markAsRead(notificationId: String): Boolean {
        val wasMarked = handleApiResultNoTransformationNoFlow(
            apiCall = { notificationClient.markAsRead(notificationId) },
            ioDispatcher = ioDispatcher
        ) is RepositoryResult.Success
        if (wasMarked) _unreadCount.update { (it - 1).coerceAtLeast(0) }
        return wasMarked
    }

    override suspend fun markAllAsRead(): Boolean {
        val wereMarked = handleApiResultNoTransformationNoFlow(
            apiCall = notificationClient::markAllAsRead,
            ioDispatcher = ioDispatcher
        ) is RepositoryResult.Success
        if (wereMarked) _unreadCount.value = 0
        return wereMarked
    }
}
