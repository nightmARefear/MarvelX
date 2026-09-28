package com.alexgreece45.marvelx.ui.screens.notifications.model

import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

internal interface NotificationsContract {

    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnBackClicked : Event
        data object OnEndOfListScrolled : Event
        data object OnPullToRefresh : Event
        data object OnErrorRetryClicked : Event
        data object OnMarkAllAsReadClicked : Event
        data class OnNotificationClicked(val notification: NotificationUi) : Event
    }

    /**
     * @param currentPage The last page that arrived. `0` until the first page arrives.
     * @param hasReachedEnd `true` once a page came back empty, so no more pages are requested.
     * @param hasError `true` when the first page failed; later failures show a snackbar instead.
     */
    data class State(
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val isFetchingNewItems: Boolean = false,
        val hasError: Boolean = false,
        val currentPage: Int = 0,
        val hasReachedEnd: Boolean = false,
        val notifications: PersistentList<NotificationUi> = persistentListOf(),
    ) : BaseState {
        val hasUnread: Boolean get() = notifications.any { !it.isRead }
    }

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class OpenDeepLinkAction(val deepLinkAction: DeepLinkAction) : Navigation
        }
    }
}
