package com.alexgreece45.marvelx.ui.screens.friends.model

import com.alexgreece45.marvelx.domain.model_api.user.block.BlockedUserDomain
import com.alexgreece45.marvelx.domain.model_api.user.friend.FriendDomain
import com.alexgreece45.marvelx.domain.model_api.user.friendRequest.IncomingFriendRequestDomain
import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState

sealed interface FriendsContract {

    sealed interface Event : BaseEvent {
        data object Init : Event

        data object OnBackClicked : Event
        data object OnSystemBack : Event

        data object OnPullToRefresh : Event

        data class OnTabClicked(val tabIndex: Int) : Event

        sealed interface AllTabEvent : Event {
            data class OnUserClicked(val userId: UserId) : AllTabEvent
            data class OnRemoveClicked(val userId: UserId) : AllTabEvent
        }

        sealed interface RequestsTabEvent : Event {
            data class OnUserClicked(val userId: UserId) : RequestsTabEvent
            data class OnRequestAccepted(val incomingFriendRequest: IncomingFriendRequestDomain) :
                RequestsTabEvent

            data class OnRequestDeclined(val userId: UserId) : RequestsTabEvent
        }

        sealed interface BlockedTabEvent : Event {
            data class OnUnblockClicked(val blockedUserDomain: BlockedUserDomain) : BlockedTabEvent
        }
    }

    /**
     * @param isLoading The generic loading of the screen. Only used to initialize the tabs.
     * @param isTabLoading The loading when a different tab is selected.
     * @param isTabRefreshing The loading (refresh animation) when the current tab has been pulled
     * to refresh.
     *
     */
    data class State(
        val isLoading: Boolean = true,
        val isTabLoading: Boolean = false,
        val isTabRefreshing: Boolean = false,

        val tabTitles: List<String> = emptyList(),
        val selectedTabIndex: Int = 0,

        val allFriends: List<FriendDomain> = emptyList(),
        val incomingFriendRequests: List<IncomingFriendRequestDomain> = emptyList(),
        val blockedUsers: List<BlockedUserDomain> = emptyList(),

        val errorView: FriendsErrorView? = null,
        val dialogView: FriendsDialogView? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {

        sealed interface Navigation : Effect {
            data object GoBack : Navigation
            data class NavigateToProfileDetailsScreen(val userId: String) : Navigation
        }
    }
}