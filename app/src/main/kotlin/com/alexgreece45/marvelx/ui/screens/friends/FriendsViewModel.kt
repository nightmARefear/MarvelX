package com.alexgreece45.marvelx.ui.screens.friends

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.GetProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.interactors.user.BlockedUserInteractor
import com.alexgreece45.marvelx.domain.interactors.user.FriendInteractor
import com.alexgreece45.marvelx.domain.interactors.user.FriendRequestInteractor
import com.alexgreece45.marvelx.domain.model_api.user.block.BlockedUserDomain
import com.alexgreece45.marvelx.domain.model_api.user.friend.FriendDomain
import com.alexgreece45.marvelx.domain.model_api.user.friendRequest.DeleteFriendRequestType
import com.alexgreece45.marvelx.domain.model_api.user.friendRequest.IncomingFriendRequestDomain
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.notifications.PushNotificationsController
import com.alexgreece45.marvelx.notifications.model.NotificationChannelType
import com.alexgreece45.marvelx.ui.screens.friends.model.FriendsContract
import com.alexgreece45.marvelx.ui.screens.friends.model.FriendsDialogView
import com.alexgreece45.marvelx.ui.screens.friends.model.FriendsErrorView
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import java.time.Instant

class FriendsViewModel(
    private val resourceProvider: ResourceProvider,
    private val userInteractor: UserInteractor,
    private val friendInteractor: FriendInteractor,
    private val friendRequestInteractor: FriendRequestInteractor,
    private val blockedUserInteractor: BlockedUserInteractor,
    private val appLogger: AppLogger,
) : BaseViewModel<FriendsContract.Event, FriendsContract.State, FriendsContract.Effect>() {

    private val loggerTag = "FriendsViewModel"

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
                event.blockedUserDomain
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
        getIncomingFriendRequests(isFromRefresh = false)
        getBlockedUsers(isFromRefresh = false)

        setupNotificationsListener()
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

        setState {
            copy(
                selectedTabIndex = tabIndex
            )
        }

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

    private fun blockedTabOnUnblockClicked(blockedUserDomain: BlockedUserDomain) {
        setState {
            copy(
                dialogView = FriendsDialogView(
                    title = resourceProvider.getString(R.string.screen_friends_dialog_unblock_user_title),
                    subtitle = resourceProvider.getString(
                        R.string.screen_friends_dialog_unblock_user_subtitle,
                        blockedUserDomain.fullName
                    ),
                    doesSubtitleContainBold = true,
                    negativeButtonLabel = resourceProvider.getString(R.string.screen_friends_dialog_unblock_user_button_negative),
                    negativeButtonOnClicked = ::hideDialogView,
                    positiveButtonLabel = resourceProvider.getString(R.string.screen_friends_dialog_unblock_user_button_positive),
                    positiveButtonOnClicked = {
                        hideDialogView()
                        unblockUser(userId = blockedUserDomain.userId)
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
            friendInteractor.getCurrentUserFriends().collect { result ->
                when (result) {
                    is InteractorPartialState.Error,
                    is InteractorPartialState.Failed -> {
                        setState {
                            copy(
                                isTabLoading = false,
                                isTabRefreshing = false,
                                errorView = FriendsErrorView(
                                    tabIndex = 0,
                                    subtitle = resourceProvider.getString(R.string.generic_unknown_error),
                                    primaryButtonOnClicked = { getFriends(isFromRefresh) }
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
                                    tabIndex = 0,
                                    subtitle = resourceProvider.getString(R.string.no_internet_connection_error_message),
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
            friendRequestInteractor.getIncomingFriendRequests().collect { result ->
                when (result) {
                    is InteractorPartialState.Error,
                    is InteractorPartialState.Failed -> {
                        setState {
                            copy(
                                isTabLoading = false,
                                isTabRefreshing = false,
                                errorView = FriendsErrorView(
                                    tabIndex = 1,
                                    subtitle = resourceProvider.getString(R.string.generic_unknown_error),
                                    primaryButtonOnClicked = { getIncomingFriendRequests((isFromRefresh)) }
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
                                    tabIndex = 1,
                                    subtitle = resourceProvider.getString(R.string.no_internet_connection_error_message),
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
            blockedUserInteractor.getBlockedUsers().collect { result ->
                when (result) {
                    is InteractorPartialState.Error,
                    is InteractorPartialState.Failed -> {
                        setState {
                            copy(
                                isTabLoading = false,
                                isTabRefreshing = false,
                                errorView = FriendsErrorView(
                                    tabIndex = 2,
                                    subtitle = resourceProvider.getString(R.string.generic_unknown_error),
                                    primaryButtonOnClicked = { getBlockedUsers(isFromRefresh) }
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
                                    tabIndex = 2,
                                    subtitle = resourceProvider.getString(R.string.no_internet_connection_error_message),
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

    private fun setupNotificationsListener() {
        viewModelScope.launch {
            PushNotificationsController.notification
                .filterNotNull()
                .collect { notificationData ->
                    when (notificationData.channelType) {
                        NotificationChannelType.AcceptFriendRequest -> {
                            val userId = notificationData.userId
                            val friendsSince = DateUtil.getUTCInstant() // Pretty much.

                            userInteractor.getProfileDetails(userId)
                                .collect { result ->
                                    when (result) {
                                        is GetProfileDetailsPartialState.Error,
                                        is GetProfileDetailsPartialState.Failed,
                                        GetProfileDetailsPartialState.NoInternetConnection -> {
                                            appLogger.e(loggerTag) { "(setupNotificationsListener) An error occurred whilst retrieving the details for user w/ID: $userId" }

                                            setState {
                                                copy(
                                                    incomingFriendRequests = incomingFriendRequests.filterNot { it.userId == notificationData.userId }
                                                )
                                            }
                                        }

                                        is GetProfileDetailsPartialState.Success -> {
                                            setState {
                                                copy(
                                                    allFriends = buildList {
                                                        if (!allFriends.map { it.userId }
                                                                .contains(userId)) {
                                                            add(
                                                                with(result.profileDetailsDomain.basicProfileDetails) {
                                                                    FriendDomain(
                                                                        userId = userId,
                                                                        fullName = fullName,
                                                                        profileImageUrl = profileImageUrlData.imageUrl,
                                                                        friendsSince = friendsSince
                                                                    )
                                                                }
                                                            )
                                                        }

                                                        addAll(allFriends)
                                                    },
                                                    incomingFriendRequests = incomingFriendRequests.filterNot { it.userId == notificationData.userId }
                                                )
                                            }
                                        }
                                    }
                                }
                        }

                        NotificationChannelType.IncomingFriendRequest -> {
                            val userId = notificationData.userId
                            val receivedAt = DateUtil.getUTCInstant() // Pretty much.

                            userInteractor.getProfileDetails(userId)
                                .collect { result ->
                                    when (result) {
                                        is GetProfileDetailsPartialState.Error,
                                        is GetProfileDetailsPartialState.Failed,
                                        GetProfileDetailsPartialState.NoInternetConnection -> {
                                            appLogger.e(loggerTag) { "(setupNotificationsListener) An error occurred whilst retrieving the details for user w/ID: $userId" }
                                        }

                                        is GetProfileDetailsPartialState.Success -> {
                                            setState {
                                                copy(
                                                    incomingFriendRequests = buildList {
                                                        if (!incomingFriendRequests.map { it.userId }
                                                                .contains(userId)) {
                                                            add(
                                                                with(result.profileDetailsDomain.basicProfileDetails) {
                                                                    IncomingFriendRequestDomain(
                                                                        userId = userId,
                                                                        fullName = fullName,
                                                                        profileImageUrl = profileImageUrlData.imageUrl,
                                                                        receivedAt = receivedAt
                                                                    )
                                                                }
                                                            )
                                                        }
                                                        addAll(incomingFriendRequests)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                        }

                        else -> {
                            // We do not care about other cases.
                        }
                    }
                }
        }
    }

    private fun removeFriend(userId: String) {
        viewModelScope.launch {
            friendInteractor.deleteFriend(userId).collect { wasFriendDeleted ->
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
            friendRequestInteractor.acceptFriendRequest(userId = incomingFriendRequest.userId)
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
                                fullName = fullName,
                                profileImageUrl = profileImageUrl,
                                friendsSince = Instant.now()
                            )
                        }

                        setState {
                            copy(
                                isTabRefreshing = false,
                                allFriends = buildList {
                                    add(newFriend)
                                    addAll(allFriends)
                                },
                                incomingFriendRequests = incomingFriendRequests.filterNot { it.userId == incomingFriendRequest.userId }
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
            friendRequestInteractor.deleteFriendRequest(
                userId = userId,
                deleteFriendRequestType = DeleteFriendRequestType.RECEIVER
            ).collect { wasFriendRequestDeleted ->
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
            blockedUserInteractor.unblockUser(userId)
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
