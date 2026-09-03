package com.alexgreece45.marvelx.common_ui.messageBanner

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerActionType
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerData
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerDuration
import com.alexgreece45.marvelx.common_ui.messageBanner.model.MessageBannerEvent
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.delay
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun MessageBanner(
    messageBannerEvents: SingleObserverFlow<MessageBannerEvent>,
    content: @Composable () -> Unit
) {
    var currentMessageBannerData by remember { mutableStateOf<MessageBannerData?>(null) }

    ObserveUiEffect(flow = messageBannerEvents, tag = "MessageBanner") { messageBannerEvent ->
        when (messageBannerEvent) {
            is MessageBannerEvent.Show -> {
                currentMessageBannerData = messageBannerEvent.messageBannerData
            }

            is MessageBannerEvent.OnDismissed -> {
                if (messageBannerEvent.uuid == currentMessageBannerData?.uuid) {
                    currentMessageBannerData = null
                }
            }
        }
    }

    // Auto-dismiss timeout logic
    currentMessageBannerData?.let { bannerData ->
        LaunchedEffect(bannerData) {
            when (val messageBannerDuration = bannerData.messageBannerDuration) {
                MessageBannerDuration.Short -> delay(4000.milliseconds)
                MessageBannerDuration.Long -> delay(10000.milliseconds)
                MessageBannerDuration.Indefinite -> awaitCancellation()
                is MessageBannerDuration.Custom -> delay(timeMillis = messageBannerDuration.durationMillis)
            }

            bannerData.onDismiss.invoke(bannerData.uuid)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        AnimatedVisibility(
            visible = currentMessageBannerData != null,
            modifier = Modifier.align(Alignment.TopCenter),
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> -fullHeight }
            ) + fadeIn(),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> -fullHeight }
            ) + fadeOut()
        ) {
            currentMessageBannerData?.let { bannerData ->
                MessageBannerContent(
                    messageBannerData = bannerData,
                    hideMessageBanner = {
                        bannerData.onDismiss.invoke(bannerData.uuid)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalAtomicApi::class)
@Composable
private fun MessageBannerContent(
    messageBannerData: MessageBannerData,
    hideMessageBanner: () -> Unit
) {
    val throttleDuration = 1500L
    val lastClickedMillis = remember { AtomicLong(0L) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                enabled = messageBannerData.actionData != null,
                role = Role.Button,
                onClick = {
                    val actionType = messageBannerData.actionData?.actionType ?: return@clickable

                    val currentTimeMillis = System.currentTimeMillis()
                    if (currentTimeMillis - lastClickedMillis.load() > throttleDuration) {
                        lastClickedMillis.store(currentTimeMillis)

                        when (actionType) {
                            MessageBannerActionType.Hide -> hideMessageBanner()
                            is MessageBannerActionType.Custom -> {
                                actionType.action.invoke()
                                hideMessageBanner()
                            }
                        }
                    }
                }
            ),
        color = MaterialTheme.colors.surface,
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = messageBannerData.message,
                overflow = TextOverflow.Ellipsis,
                maxLines = 2
            )

            messageBannerData.actionData?.let { actionData ->
                Spacer(modifier = Modifier.weight(1f))

                MessageBannerTextButton(
                    label = actionData.label,
                    throttleDuration = throttleDuration,
                    lastClickedMillis = lastClickedMillis,
                    onClick = {
                        when (val actionType = actionData.actionType) {
                            MessageBannerActionType.Hide -> hideMessageBanner()
                            is MessageBannerActionType.Custom -> {
                                actionType.action.invoke()
                                hideMessageBanner()
                            }
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalAtomicApi::class)
@Composable
private fun MessageBannerTextButton(
    label: String,
    throttleDuration: Long,
    lastClickedMillis: AtomicLong,
    onClick: () -> Unit
) {
    TextButton(
        onClick = {
            val currentTimeMillis = System.currentTimeMillis()
            if (currentTimeMillis - lastClickedMillis.load() > throttleDuration) {
                lastClickedMillis.store(currentTimeMillis)
                onClick()
            }
        }
    ) {
        Text(
            text = label,
            textDecoration = TextDecoration.Underline,
            maxLines = 1
        )
    }
}