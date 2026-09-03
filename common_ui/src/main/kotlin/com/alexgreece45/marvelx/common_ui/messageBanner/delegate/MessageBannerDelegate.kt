package com.alexgreece45.marvelx.common_ui.messageBanner.delegate

import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerActionData
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerData
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerDuration
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerEvent
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.receiveAsSingleObserverFlow
import kotlinx.coroutines.channels.Channel
import org.koin.core.annotation.Factory
import java.util.UUID

interface MessageBannerDelegate {
    val messageBannerEvents: SingleObserverFlow<MessageBannerEvent>

    fun showMessageBanner(
        message: String,
        messageBannerDuration: MessageBannerDuration = MessageBannerDuration.Short,
        actionData: MessageBannerActionData? = null
    )

    fun dismissMessageBanner(uuid: UUID)
}

@Factory(binds = [MessageBannerDelegate::class])
internal class MessageBannerDelegateImpl : MessageBannerDelegate {

    private val _messageBannerEvents: Channel<MessageBannerEvent> = Channel(capacity = 1)

    override val messageBannerEvents: SingleObserverFlow<MessageBannerEvent> =
        _messageBannerEvents.receiveAsSingleObserverFlow()

    override fun showMessageBanner(
        message: String,
        messageBannerDuration: MessageBannerDuration,
        actionData: MessageBannerActionData?
    ) {
        val newUuid = UUID.randomUUID()
        val messageBannerData = MessageBannerData(
            uuid = newUuid,
            message = message,
            messageBannerDuration = messageBannerDuration,
            actionData = actionData,
            onDismiss = { dismissMessageBanner(newUuid) }
        )
        _messageBannerEvents.trySend(MessageBannerEvent.Show(messageBannerData))
    }

    override fun dismissMessageBanner(uuid: UUID) {
        _messageBannerEvents.trySend(MessageBannerEvent.OnDismissed(uuid))
    }
}