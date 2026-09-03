package com.alexgreece45.marvelx.common_ui.messageBanner.model

sealed interface MessageBannerActionType {
    data object Hide : MessageBannerActionType
    data class Custom(val action: () -> Unit) : MessageBannerActionType
}