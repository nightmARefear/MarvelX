package com.alexgreece45.marvelx.domain.interactors.notification

import com.alexgreece45.marvelx.data.repository.NotificationRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResultNoFlow
import com.alexgreece45.marvelx.domain.model_api.notification.NotificationsPageDomain
import com.alexgreece45.marvelx.domain.model_api.notification.toNotificationsPageDomain
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.annotation.Factory

interface NotificationInteractor {

    val unreadCount: StateFlow<Int>

    suspend fun getNotifications(page: Int): InteractorPartialState<NotificationsPageDomain>
    suspend fun refreshUnreadCount()

    /** Only call for an unread notification; the unread badge is decremented on success. */
    suspend fun markAsRead(notificationId: String): Boolean
    suspend fun markAllAsRead(): Boolean
}

@Factory
internal class NotificationInteractorImpl(
    private val notificationRepository: NotificationRepository
) : NotificationInteractor {

    override val unreadCount: StateFlow<Int> get() = notificationRepository.unreadCount

    override suspend fun getNotifications(page: Int): InteractorPartialState<NotificationsPageDomain> {
        return handleRepositoryResultNoFlow(
            repositoryResult = notificationRepository.getNotifications(page),
            transformation = { this.toNotificationsPageDomain() }
        )
    }

    override suspend fun refreshUnreadCount() {
        notificationRepository.refreshUnreadCount()
    }

    override suspend fun markAsRead(notificationId: String): Boolean =
        notificationRepository.markAsRead(notificationId)

    override suspend fun markAllAsRead(): Boolean =
        notificationRepository.markAllAsRead()
}
