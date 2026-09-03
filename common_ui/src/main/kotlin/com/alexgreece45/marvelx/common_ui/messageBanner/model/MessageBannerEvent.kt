package com.alexgreece45.marvelx.common_ui.messageBanner.model

import java.util.UUID

sealed interface MessageBannerEvent {
    data class Show(val messageBannerData: MessageBannerData) : MessageBannerEvent
    data class OnDismissed(val uuid: UUID) : MessageBannerEvent
}