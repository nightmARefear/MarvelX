package com.alexgreece45.marvelx.ui.screens.friends

internal object FriendsTestTags {
    // FriendsContract.Event.OnBackClicked
    const val BACK_BUTTON = "friends_back_button"

    // FriendsContract.Event.OnTabClicked(0)
    const val ALL_TAB = "friends_all_tab"

    // FriendsContract.Event.OnTabClicked(1)
    const val REQUESTS_TAB = "friends_requests_tab"

    // FriendsContract.Event.OnTabClicked(2)
    const val BLOCKED_TAB = "friends_blocked_tab"

    // Error `primaryButtonOnClicked`
    const val ERROR_RETRY_BUTTON = "friends_error_retry_button"

    // The confirmation dialog buttons use the shared alert dialog tags
    // (`alert_dialog_confirm_button`, `alert_dialog_dismiss_button`).

    /** FriendsContract.Event.OnTabClicked; keyed by the tab position (0 = all, 1 = requests, 2 = blocked). */
    fun tab(index: Int): String = when (index) {
        0 -> ALL_TAB
        1 -> REQUESTS_TAB
        else -> BLOCKED_TAB
    }

    /**
     * FriendsContract.Event.AllTabEvent.OnUserClicked; one tag per friend, keyed by the user id
     * (e.g. `friends_friend_42_tile`).
     */
    fun friendTile(userId: Any) = "friends_friend_${userId}_tile"

    /** FriendsContract.Event.AllTabEvent.OnRemoveClicked; one tag per friend. */
    fun removeFriendButton(userId: Any) = "friends_friend_${userId}_remove_button"

    /** FriendsContract.Event.RequestsTabEvent.OnUserClicked; one tag per request. */
    fun requestTile(userId: Any) = "friends_request_${userId}_tile"

    /** FriendsContract.Event.RequestsTabEvent.OnRequestAccepted; one tag per request. */
    fun acceptRequestButton(userId: Any) = "friends_request_${userId}_accept_button"

    /** FriendsContract.Event.RequestsTabEvent.OnRequestDeclined; one tag per request. */
    fun declineRequestButton(userId: Any) = "friends_request_${userId}_decline_button"

    /** FriendsContract.Event.BlockedTabEvent.OnUnblockClicked; one tag per blocked user. */
    fun unblockButton(userId: Any) = "friends_blocked_${userId}_unblock_button"
}
