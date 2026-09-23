package com.alexgreece45.marvelx.ui.screens.chat.inbox

internal object InboxTestTags {
    // InboxContract.Event.OnErrorButtonClicked
    const val ERROR_RETRY_BUTTON = "inbox_error_retry_button"

    // InboxContract.Event.OnRefresh (empty inbox `Retry` button)
    const val EMPTY_INBOX_REFRESH_BUTTON = "inbox_empty_inbox_refresh_button"

    /**
     * InboxContract.Event.OnConversationClicked; one tag per conversation, keyed by the conversation
     * id (e.g. `inbox_conversation_42_row`).
     */
    fun conversationRow(conversationId: Any) = "inbox_conversation_${conversationId}_row"
}
