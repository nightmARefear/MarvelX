package com.alexgreece45.marvelx.ui.screens.notifications

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.common_ui.snackbar.delegate.SnackbarDelegate
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.notification.NotificationInteractor
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.notifications.model.NotificationUi
import com.alexgreece45.marvelx.ui.screens.notifications.model.NotificationsContract
import com.alexgreece45.marvelx.ui.screens.notifications.model.toUi
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel(binds = [NotificationsViewModel::class])
internal class NotificationsViewModel(
    private val notificationInteractor: NotificationInteractor,
    private val resourceProvider: ResourceProvider,
    snackbarDelegate: SnackbarDelegate,
) : BaseViewModel<NotificationsContract.Event, NotificationsContract.State, NotificationsContract.Effect>(),
    SnackbarDelegate by snackbarDelegate {

    private var fetchJob: Job? = null

    override fun setInitialState(): NotificationsContract.State = NotificationsContract.State()

    override fun handleEvents(event: NotificationsContract.Event) {
        when (event) {
            NotificationsContract.Event.Init -> init()
            NotificationsContract.Event.OnBackClicked -> onBackClicked()
            NotificationsContract.Event.OnEndOfListScrolled -> onEndOfListScrolled()
            NotificationsContract.Event.OnPullToRefresh -> onPullToRefresh()
            NotificationsContract.Event.OnErrorRetryClicked -> onErrorRetryClicked()
            NotificationsContract.Event.OnMarkAllAsReadClicked -> onMarkAllAsReadClicked()
            is NotificationsContract.Event.OnNotificationClicked -> onNotificationClicked(event.notification)
        }
    }

    // Event functions.

    /**
     * Runs every time the screen enters composition (e.g. when coming back from a notification's
     * screen), so the first page is only requested once. Pull to refresh reloads it.
     */
    private fun init() {
        if (currentState.currentPage > 0 || fetchJob?.isActive == true) return

        fetchPage(page = 1)
    }

    private fun onBackClicked() {
        setEffect {
            NotificationsContract.Effect.Navigation.GoBack
        }
    }

    private fun onEndOfListScrolled() {
        if (currentState.isLoading
            || currentState.isRefreshing
            || currentState.isFetchingNewItems
            || currentState.hasReachedEnd
            || currentState.notifications.isEmpty()
        ) return

        setState {
            copy(isFetchingNewItems = true)
        }
        fetchPage(page = currentState.currentPage + 1)
    }

    private fun onPullToRefresh() {
        fetchJob?.cancel()
        setState {
            copy(
                isRefreshing = true,
                isFetchingNewItems = false
            )
        }
        fetchPage(page = 1)
    }

    private fun onErrorRetryClicked() {
        setState {
            copy(
                isLoading = true,
                hasError = false
            )
        }
        fetchPage(page = 1)
    }

    private fun onMarkAllAsReadClicked() {
        if (!currentState.hasUnread) return

        val previouslyUnreadIds = currentState.notifications
            .filterNot { it.isRead }
            .map { it.notificationId }
            .toSet()
        setState {
            copy(notifications = notifications.map { it.copy(isRead = true) }.toPersistentList())
        }
        viewModelScope.launch {
            if (!notificationInteractor.markAllAsRead()) {
                // Only undo the rows this call changed; a page may have arrived in the meantime.
                setState {
                    copy(
                        notifications = notifications.map { notification ->
                            if (notification.notificationId in previouslyUnreadIds) notification.copy(
                                isRead = false
                            ) else notification
                        }.toPersistentList()
                    )
                }
                showErroneousSnackbar(
                    errorMessage = resourceProvider.getString(R.string.screen_notifications_error_marking_all_as_read)
                )
            }
        }
    }

    /**
     * Opens the notification right away; marking it read happens in the background and is undone
     * if the request fails.
     */
    private fun onNotificationClicked(notification: NotificationUi) {
        if (!notification.isRead) {
            setNotificationRead(notificationId = notification.notificationId, isRead = true)
            viewModelScope.launch {
                if (!notificationInteractor.markAsRead(notification.notificationId)) {
                    setNotificationRead(
                        notificationId = notification.notificationId,
                        isRead = false
                    )
                }
            }
        }

        setEffect {
            NotificationsContract.Effect.Navigation.OpenDeepLinkAction(notification.deepLinkAction)
        }
    }

    // Helper functions.

    /**
     * Fetches [page]. [NotificationsContract.State.currentPage] only advances once the page
     * arrives, so a failed page is requested again on the next end-of-list scroll instead of being
     * skipped. Page `1` replaces the list (first load, refresh, retry) and resyncs the badge.
     */
    private fun fetchPage(page: Int) {
        fetchJob = viewModelScope.launch {
            when (val result = notificationInteractor.getNotifications(page)) {
                is InteractorPartialState.Success -> {
                    val newItems = result.data.items.map { it.toUi() }

                    setState {
                        val existingItems = if (page == 1) persistentListOf() else notifications
                        val existingIds = existingItems.map { it.notificationId }.toSet()
                        copy(
                            isLoading = false,
                            isRefreshing = false,
                            isFetchingNewItems = false,
                            hasError = false,
                            currentPage = page,
                            hasReachedEnd = result.data.isLastPage,
                            notifications = existingItems.addAll(
                                newItems.filterNot { it.notificationId in existingIds }
                                    .toPersistentList()
                            )
                        )
                    }

                    if (page == 1) notificationInteractor.refreshUnreadCount()
                }

                is InteractorPartialState.Error,
                is InteractorPartialState.Failed,
                InteractorPartialState.NoInternetConnection -> {
                    val hasItems = currentState.notifications.isNotEmpty()
                    setState {
                        copy(
                            isLoading = false,
                            isRefreshing = false,
                            isFetchingNewItems = false,
                            // With items on screen, keep them and report the failure in a snackbar.
                            hasError = !hasItems
                        )
                    }

                    if (hasItems) {
                        showErroneousSnackbar(
                            errorMessage = resourceProvider.getString(R.string.screen_notifications_error_loading_more)
                        )
                    }
                }
            }
        }
    }

    private fun setNotificationRead(notificationId: String, isRead: Boolean) {
        setState {
            copy(
                notifications = notifications.map { notification ->
                    if (notification.notificationId == notificationId) notification.copy(isRead = isRead) else notification
                }.toPersistentList()
            )
        }
    }
}
