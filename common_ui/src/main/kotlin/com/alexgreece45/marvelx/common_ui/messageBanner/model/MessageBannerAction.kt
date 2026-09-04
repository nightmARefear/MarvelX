package com.alexgreece45.marvelx.common_ui.messageBanner.model

sealed interface MessageBannerAction {
    val actionType: MessageBannerActionType
    val label: String?

    data class Global(
        override val actionType: MessageBannerActionType,
        override val label: String? = null
    ) : MessageBannerAction

    data class Button(
        override val actionType: MessageBannerActionType,
        override val label: String
    ) : MessageBannerAction
}