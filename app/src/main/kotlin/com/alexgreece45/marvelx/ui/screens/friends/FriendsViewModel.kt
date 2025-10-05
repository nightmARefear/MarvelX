package com.alexgreece45.marvelx.ui.screens.friends

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.data.repository.FriendRequestsRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.model_api.user.friend.FriendDomain
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.friends.model.FriendsContract
import com.alexgreece45.marvelx.ui.screens.friends.model.FriendsDialogView
import com.alexgreece45.marvelx.ui.screens.friends.model.FriendsErrorView
import com.alexgreece45.marvelx.ui.screens.friends.model.IncomingFriendRequestDomain
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.toFullName
import kotlinx.coroutines.launch

class FriendsViewModel(
    private val resourceProvider: ResourceProvider,
    private val userInteractor: UserInteractor,
    private val prefKeys: PrefKeys,
    private val friendRequestsRepository: FriendRequestsRepository,
) : BaseViewModel<FriendsContract.Event, FriendsContract.State, FriendsContract.Effect>() {

    override fun setInitialState(): FriendsContract.State = FriendsContract.State()

    override fun handleEvents(event: FriendsContract.Event) {
        when (event) {
            FriendsContract.Event.Init -> init()

            FriendsContract.Event.OnBackClicked -> onBackClicked()
            FriendsContract.Event.OnSystemBack -> onSystemBack()

            FriendsContract.Event.OnPullToRefresh -> onPullToRefresh()

            is FriendsContract.Event.OnTabClicked -> onTabClicked(event.tabIndex)

            // All tab
            is FriendsContract.Event.AllTabEvent.OnUserClicked -> allTabOnUserClicked(event.userId)
            is FriendsContract.Event.AllTabEvent.OnRemoveClicked -> allTabOnRemoveClicked(event.userId)

            // Requests tab
            is FriendsContract.Event.RequestsTabEvent.OnUserClicked -> requestsTabOnUserClicked(
                event.userId
            )

            is FriendsContract.Event.RequestsTabEvent.OnRequestAccepted -> requestsTabOnRequestAccepted(
                event.incomingFriendRequest
            )

            is FriendsContract.Event.RequestsTabEvent.OnRequestDeclined -> requestsTabOnRequestDeclined(
                event.userId
            )

            // Blocked tab
            is FriendsContract.Event.BlockedTabEvent.OnUnblockClicked -> blockedTabOnUnblockClicked(
                event.userId
            )
        }
    }

    // ************************** EVENT FUNCTIONS **************************

    private fun init() {
        setState {
            copy(
                isLoading = true,
                errorView = null,
                tabTitles = listOf(
                    resourceProvider.getString(R.string.screen_friends_tab_all_title),
                    resourceProvider.getString(R.string.screen_friends_tab_requests_title),
                    resourceProvider.getString(R.string.screen_friends_tab_blocked_title),
                )
            )
        }

        setState {
            copy(
                isLoading = false
            )
        }

        getFriends(isFromRefresh = false)
    }

    private fun onBackClicked() {
        setEffect {
            FriendsContract.Effect.Navigation.GoBack
        }
    }

    private fun onSystemBack() {
        if (currentState.dialogView != null) {
            setState {
                copy(
                    dialogView = null
                )
            }
        } else {
            setEffect {
                FriendsContract.Effect.Navigation.GoBack

            }
        }
    }

    private fun onPullToRefresh() {
        if (currentState.isLoading || currentState.isTabLoading || currentState.isTabRefreshing) return

        when (currentState.selectedTabIndex) {
            0 -> getFriends(isFromRefresh = true)
            1 -> getIncomingFriendRequests(isFromRefresh = true)
            2 -> getBlockedUsers(isFromRefresh = true)
        }
    }

    private fun onTabClicked(tabIndex: Int) {
        if (currentState.selectedTabIndex == tabIndex) return

        when (tabIndex) {
            0 -> getFriends(isFromRefresh = false)
            1 -> getIncomingFriendRequests(isFromRefresh = false)
            2 -> getBlockedUsers(isFromRefresh = false)
        }
    }

    private fun allTabOnUserClicked(userId: String) {
        setEffect {
            FriendsContract.Effect.Navigation.NavigateToProfileDetailsScreen(userId = userId)
        }
    }

    private fun allTabOnRemoveClicked(userId: String) {
        setState {
            copy(
                dialogView = FriendsDialogView(
                    title = resourceProvider.getString(R.string.screen_friends_dialog_remove_friend_title),
                    subtitle = resourceProvider.getString(R.string.screen_friends_dialog_remove_friend_subtitle),
                    negativeButtonLabel = resourceProvider.getString(R.string.screen_friends_dialog_remove_friend_button_negative),
                    negativeButtonOnClicked = ::hideDialogView,
                    positiveButtonLabel = resourceProvider.getString(R.string.screen_friends_dialog_remove_friend_button_positive),
                    positiveButtonOnClicked = {
                        hideDialogView()
                        removeFriend(userId)
                    }
                )
            )
        }
    }

    private fun requestsTabOnUserClicked(userId: String) {
        setEffect {
            FriendsContract.Effect.Navigation.NavigateToProfileDetailsScreen(userId = userId)
        }
    }

    private fun requestsTabOnRequestAccepted(incomingFriendRequest: IncomingFriendRequestDomain) {
        hideDialogView()
        acceptFriendRequest(incomingFriendRequest)
    }

    private fun requestsTabOnRequestDeclined(userId: String) {
        hideDialogView()
        removeFriendRequest(userId)
    }

    private fun blockedTabOnUnblockClicked(userId: String) {
        setState {
            copy(
                dialogView = FriendsDialogView(
                    title = resourceProvider.getString(R.string.screen_friends_dialog_unblock_user_title),
                    subtitle = resourceProvider.getString(R.string.screen_friends_dialog_unblock_user_subtitle),
                    negativeButtonLabel = resourceProvider.getString(R.string.screen_friends_dialog_unblock_user_button_negative),
                    negativeButtonOnClicked = ::hideDialogView,
                    positiveButtonLabel = resourceProvider.getString(R.string.screen_friends_dialog_unblock_user_button_positive),
                    positiveButtonOnClicked = {
                        hideDialogView()
                        unblockUser(userId)
                    }
                )
            )
        }
    }

    // ************************** DIALOG HELPER FUNCTIONS **************************

    private fun hideDialogView() {
        setState {
            copy(
                dialogView = null
            )
        }
    }


    // ************************** DATA HELPER FUNCTIONS **************************

    /**
     * Returns the friends of the current user.
     *
     * @param isFromRefresh Whether the action was triggered from the `pull to refresh` functionality.
     */
    private fun getFriends(isFromRefresh: Boolean) {
        if (isFromRefresh) {
            setState {
                copy(
                    isTabRefreshing = true
                )
            }
        } else {
            setState {
                copy(
                    isTabLoading = true
                )
            }
        }

        viewModelScope.launch {
            userInteractor.getFriends(userId = prefKeys.getUserId()).collect { result ->
                when (result) {
                    is InteractorPartialState.Error,
                    is InteractorPartialState.Failed -> {
                        setState {
                            copy(
                                isTabLoading = false,
                                isTabRefreshing = false,
                                errorView = FriendsErrorView(
                                    subtitle = resourceProvider.getString(R.string.generic_unknown_error),
                                    primaryButtonLabel = resourceProvider.getString(R.string.generic_go_back),
                                    primaryButtonOnClicked = {
                                        setEffect {
                                            FriendsContract.Effect.Navigation.GoBack
                                        }
                                    }
                                )
                            )
                        }
                    }

                    InteractorPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isTabLoading = false,
                                isTabRefreshing = false,
                                errorView = FriendsErrorView(
                                    subtitle = resourceProvider.getString(R.string.no_internet_connection_error_message),
                                    primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                    primaryButtonOnClicked = { getFriends(isFromRefresh) }
                                )
                            )
                        }
                    }

                    is InteractorPartialState.Success -> {
                        setState {
                            copy(
                                isTabLoading = false,
                                isTabRefreshing = false,
                                errorView = null,
                                allFriends = result.data
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getIncomingFriendRequests(isFromRefresh: Boolean) {
        if (isFromRefresh) {
            setState {
                copy(
                    isTabRefreshing = true
                )
            }
        } else {
            setState {
                copy(
                    isTabLoading = true
                )
            }
        }

        viewModelScope.launch {
            userInteractor.getIncomingFriendRequests().collect { result ->
                when (result) {
                    is InteractorPartialState.Error,
                    is InteractorPartialState.Failed -> {
                        setState {
                            copy(
                                isTabLoading = false,
                                isTabRefreshing = false,
                                errorView = FriendsErrorView(
                                    subtitle = resourceProvider.getString(R.string.generic_unknown_error),
                                    primaryButtonLabel = resourceProvider.getString(R.string.generic_go_back),
                                    primaryButtonOnClicked = {
                                        setEffect {
                                            FriendsContract.Effect.Navigation.GoBack
                                        }
                                    }
                                )
                            )
                        }
                    }

                    InteractorPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isTabLoading = false,
                                isTabRefreshing = false,
                                errorView = FriendsErrorView(
                                    subtitle = resourceProvider.getString(R.string.no_internet_connection_error_message),
                                    primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                    primaryButtonOnClicked = { getIncomingFriendRequests((isFromRefresh)) }
                                )
                            )
                        }
                    }

                    is InteractorPartialState.Success -> {
                        setState {
                            copy(
                                isTabLoading = false,
                                isTabRefreshing = false,
                                errorView = null,
                                incomingFriendRequests = result.data
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getBlockedUsers(isFromRefresh: Boolean) {
        if (isFromRefresh) {
            setState {
                copy(
                    isTabRefreshing = true
                )
            }
        } else {
            setState {
                copy(
                    isTabLoading = true
                )
            }
        }

        viewModelScope.launch {
            userInteractor.getBlockedUsers().collect { result ->
                when (result) {
                    is InteractorPartialState.Error,
                    is InteractorPartialState.Failed -> {
                        setState {
                            copy(
                                isTabLoading = false,
                                isTabRefreshing = false,
                                errorView = FriendsErrorView(
                                    subtitle = resourceProvider.getString(R.string.generic_unknown_error),
                                    primaryButtonLabel = resourceProvider.getString(R.string.generic_go_back),
                                    primaryButtonOnClicked = {
                                        setEffect {
                                            FriendsContract.Effect.Navigation.GoBack
                                        }
                                    }
                                )
                            )
                        }
                    }

                    InteractorPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isTabLoading = false,
                                isTabRefreshing = false,
                                errorView = FriendsErrorView(
                                    subtitle = resourceProvider.getString(R.string.no_internet_connection_error_message),
                                    primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                    primaryButtonOnClicked = { getBlockedUsers(isFromRefresh) }
                                )
                            )
                        }
                    }

                    is InteractorPartialState.Success -> {
                        setState {
                            copy(
                                isTabLoading = false,
                                isTabRefreshing = false,
                                errorView = null,
                                blockedUsers = result.data
                            )
                        }
                    }
                }
            }
        }
    }

    private fun removeFriend(userId: String) {
        viewModelScope.launch {
            userInteractor.deleteFriend(userId).collect { wasFriendDeleted ->
                if (wasFriendDeleted) {
                    setEffect {
                        FriendsContract.Effect.ShowToastMessage(
                            message = resourceProvider.getString(
                                R.string.screen_friends_success_deleting_friend
                            )
                        )
                    }

                    setState {
                        copy(
                            isTabRefreshing = true
                        )
                    }

                    setState {
                        copy(
                            isTabRefreshing = false,
                            allFriends = allFriends.filterNot { friend -> friend.userId == userId }
                        )
                    }
                } else {
                    setEffect {
                        FriendsContract.Effect.ShowToastMessage(
                            message = resourceProvider.getString(R.string.screen_friends_error_deleting_friend)
                        )
                    }
                }
            }
        }
    }

    private fun acceptFriendRequest(incomingFriendRequest: IncomingFriendRequestDomain) {
        viewModelScope.launch {
            friendRequestsRepository.acceptFriendRequest(userId = incomingFriendRequest.userId)
                .collect { wasFriendRequestAccepted ->
                    if (wasFriendRequestAccepted) {
                        setEffect {
                            FriendsContract.Effect.ShowToastMessage(
                                message = resourceProvider.getString(
                                    R.string.screen_friends_success_accepting_friend_request
                                )
                            )
                        }

                        setState {
                            copy(
                                isTabRefreshing = true
                            )
                        }

                        val newFriend = with(incomingFriendRequest) {
                            FriendDomain(
                                userId = userId,
                                fullName = Pair(firstName, lastName).toFullName(),
                                profileImageUrl = profileImageUrl,
                                friendsSince = DateUtil.getSecondsFromEpoch()
                            )
                        }

                        setState {
                            copy(
                                isTabRefreshing = false,
                                allFriends = buildList {
                                    add(newFriend)
                                    addAll(allFriends)
                                }
                            )
                        }
                    } else {
                        setEffect {
                            FriendsContract.Effect.ShowToastMessage(
                                message = resourceProvider.getString(R.string.screen_friends_error_accepting_friend_request)
                            )
                        }
                    }
                }
        }
    }

    private fun removeFriendRequest(userId: String) {
        viewModelScope.launch {
            friendRequestsRepository.deleteFriendRequest(userId)
                .collect { wasFriendRequestDeleted ->
                    if (wasFriendRequestDeleted) {
                        setEffect {
                            FriendsContract.Effect.ShowToastMessage(
                                message = resourceProvider.getString(
                                    R.string.screen_friends_success_declining_friend_request
                                )
                            )
                        }

                        setState {
                            copy(
                                isTabRefreshing = true
                            )
                        }

                        setState {
                            copy(
                                isTabRefreshing = false,
                                incomingFriendRequests = incomingFriendRequests.filterNot { incomingFriendRequest -> incomingFriendRequest.userId == userId }
                            )
                        }
                    } else {
                        setEffect {
                            FriendsContract.Effect.ShowToastMessage(
                                message = resourceProvider.getString(R.string.screen_friends_error_declining_friend_request)
                            )
                        }
                    }
                }
        }
    }

    private fun unblockUser(userId: String) {
        viewModelScope.launch {
            userInteractor.unblockUser(userId)
                .collect { result ->
                    when (result) {
                        is InteractorPartialState.Error,
                        is InteractorPartialState.Failed,
                        InteractorPartialState.NoInternetConnection -> {
                            setEffect {
                                FriendsContract.Effect.ShowToastMessage(
                                    message = resourceProvider.getString(R.string.screen_friends_error_unblocking_user)
                                )
                            }
                        }

                        is InteractorPartialState.Success -> {
                            setEffect {
                                FriendsContract.Effect.ShowToastMessage(
                                    message = resourceProvider.getString(
                                        R.string.screen_friends_success_unblocking_user
                                    )
                                )
                            }

                            setState {
                                copy(
                                    isTabRefreshing = true
                                )
                            }

                            setState {
                                copy(
                                    isTabRefreshing = false,
                                    blockedUsers = blockedUsers.filterNot { blockedUser -> blockedUser.userId == userId }
                                )
                            }
                        }
                    }
                }
        }
    }
}
