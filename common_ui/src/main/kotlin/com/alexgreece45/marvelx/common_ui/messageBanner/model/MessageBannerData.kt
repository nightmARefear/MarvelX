package com.alexgreece45.marvelx.common_ui.messageBanner.model

import java.util.UUID

data class MessageBannerData(
    val uuid: UUID,
    val message: String,
    val messageBannerDuration: MessageBannerDuration = MessageBannerDuration.Short,
    val actionData: MessageBannerActionData? = null,
    val onDismiss: (UUID) -> Unit
)

sealed interface MessageBannerDuration {
    data object Short : MessageBannerDuration
    data object Long : MessageBannerDuration
    data object Indefinite : MessageBannerDuration
    data class Custom(val durationMillis: kotlin.Long) : MessageBannerDuration
}