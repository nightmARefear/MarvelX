package com.alexgreece45.marvelx.ui.screens.chat.conversation

internal object ConversationTestTags {
    // ConversationContract.Event.OnBack
    const val BACK_BUTTON = "conversation_back_button"

    // ConversationContract.Event.OnParticipantClicked (toolbar avatar)
    const val PARTICIPANT_AVATAR = "conversation_participant_avatar"

    // ConversationContract.Event.OnPinnedOrderItemClicked
    const val PINNED_ORDER_ITEM_CARD = "conversation_pinned_order_item_card"

    // ConversationContract.Event.OnMessageTextChanged
    const val MESSAGE_TEXT_FIELD = "conversation_message_text_field"

    // ConversationContract.Event.OnSendClicked
    const val SEND_BUTTON = "conversation_send_button"

    // ConversationContract.Event.OnErrorButtonClicked (no internet / generic error)
    const val ERROR_RETRY_BUTTON = "conversation_error_retry_button"

    // ConversationContract.Event.OnBack (forbidden error `Go back` button)
    const val FORBIDDEN_GO_BACK_BUTTON = "conversation_forbidden_go_back_button"

    /**
     * ConversationContract.Event.OnRetryMessageClicked; one tag per failed message, keyed by the
     * message list key (e.g. `conversation_message_c-123_retry_button`).
     */
    fun retryMessageButton(messageKey: String) = "conversation_message_${messageKey}_retry_button"
}
