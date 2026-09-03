package com.alexgreece45.marvelx.common_ui.snackbar

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.common_ui.components.WrapClickableText
import com.alexgreece45.marvelx.common_ui.components.model.ClickableString
import com.alexgreece45.marvelx.common_ui.extensions.hideSnackbar
import com.alexgreece45.marvelx.common_ui.extensions.showSnackbarAndHandleResult
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionType
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarEffect
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarStateData
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarType
import com.alexgreece45.marvelx.common_ui.util.ObserveUiEffect
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.ui_theme.theme.values.customColors
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MarvelXSnackbar(
    snackbarEffects: SingleObserverFlow<SnackbarEffect>,
    modifier: Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var currentSnackbarData by remember { mutableStateOf<SnackbarStateData?>(null) }

    ObserveUiEffect(flow = snackbarEffects, tag = "MarvelXSnackbar") { snackbarEffect ->
        when (snackbarEffect) {
            is SnackbarEffect.Show -> {
                currentSnackbarData = snackbarEffect.snackbarStateData

                coroutineScope.launch {
                    snackbarHostState.showSnackbarAndHandleResult(snackbarEffect.snackbarStateData)
                }
            }

            is SnackbarEffect.OnDismissed -> {
                if (snackbarEffect.uuid == currentSnackbarData?.uuid) {
                    currentSnackbarData = null
                }
            }
        }
    }

    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier
            .padding(12.dp)
            .fillMaxWidth(),
        snackbar = {
            currentSnackbarData?.let {
                MarvelXSnackbarContent(
                    snackbarStateData = it,
                    hideSnackbar = snackbarHostState::hideSnackbar
                )
            }
        }
    )
}

@Composable
private fun MarvelXSnackbarContent(snackbarStateData: SnackbarStateData, hideSnackbar: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (snackbarStateData.enableDragToDismiss && snackbarStateData.duration != SnackbarDuration.Indefinite) {
                    Modifier.dragToDismiss(hideSnackbar = hideSnackbar)
                } else {
                    Modifier
                }
            ),
        shape = MaterialTheme.shapes.medium,
        color = when (snackbarStateData.type) {
            SnackbarType.INFO -> MaterialTheme.customColors.snackbarBackgroundColors.info
            SnackbarType.WARNING -> MaterialTheme.customColors.snackbarBackgroundColors.warning
            SnackbarType.ERROR -> MaterialTheme.customColors.snackbarBackgroundColors.error
            SnackbarType.SUCCESS -> MaterialTheme.customColors.snackbarBackgroundColors.success
        },
        contentColor = MaterialTheme.customColors.white,
        elevation = 4.dp
    ) {
        Box(
            modifier = Modifier.padding(
                vertical = 8.dp,
                horizontal = 16.dp
            )
        ) {
            if (snackbarStateData.actionData != null) {
                ActionSnackbar(
                    message = snackbarStateData.message,
                    actionData = snackbarStateData.actionData,
                    hideSnackbar = hideSnackbar
                )
            } else {
                MessageSnackbar(
                    message = snackbarStateData.message,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            }
        }
    }
}

/**
 * Function that composes a snackbar with a message and an action button.
 *
 * @see MessageSnackbar()
 */
@Composable
private fun ActionSnackbar(
    message: String,
    actionData: SnackbarActionData,
    hideSnackbar: () -> Unit
) {
    if (actionData.isHyperlink) {
        HyperlinkSnackbar(
            message = message,
            actionData = actionData,
            hideSnackbar = hideSnackbar
        )
        return
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        MessageSnackbar(
            message = message,
            modifier = Modifier.weight(1f)
        )

        TextButton(
            onClick = {
                when (val actionType = actionData.actionType) {
                    SnackbarActionType.Hide -> hideSnackbar()
                    is SnackbarActionType.Custom -> actionType.onAction.invoke()
                }
            },
        ) {
            Text(
                text = actionData.label
            )
        }
    }
}

/**
 * Function that composes a snackbar with a message.
 *
 * @see ActionSnackbar
 */
@Composable
private fun MessageSnackbar(message: String, modifier: Modifier) {
    return Text(
        text = message,
        modifier = modifier,
        overflow = TextOverflow.Ellipsis,
        maxLines = 2,
        style = MaterialTheme.typography.h6
    )
}

@Composable
private fun HyperlinkSnackbar(
    message: String,
    actionData: SnackbarActionData,
    hideSnackbar: () -> Unit
) {
    WrapClickableText(
        text = message,
        textStyle = MaterialTheme.typography.h6,
        clickableStrings = listOf(
            ClickableString(
                text = actionData.label,
                onClick = {
                    when (val actionType = actionData.actionType) {
                        SnackbarActionType.Hide -> hideSnackbar()
                        is SnackbarActionType.Custom -> actionType.onAction.invoke()
                    }
                }
            )
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

/**
 * @param dismissThreshold Pixel threshold required to trigger dismissal. Defaults to `80`.
 */
@Composable
private fun Modifier.dragToDismiss(
    dismissThreshold: Float = 80f,
    hideSnackbar: () -> Unit
): Modifier {
    val coroutineScope = rememberCoroutineScope()

    // Tracks the vertical drag displacement in pixels
    val offsetY = remember { Animatable(0f) }

    return this
        .offset { IntOffset(0, offsetY.value.roundToInt()) }
        .pointerInput(Unit) {
            detectVerticalDragGestures(
                onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch {
                        // If the snackbar is at the bottom of the screen, restrict to dragging DOWN (dragAmount > 0)
                        // If we want to allow dragging UP, remove the coerceAtLeast check or adapt as needed.
                        val newOffset = (offsetY.value + dragAmount).coerceAtLeast(0f)
                        offsetY.snapTo(newOffset)
                    }
                },
                onDragEnd = {
                    coroutineScope.launch {
                        if (offsetY.value > dismissThreshold) {
                            // Animate off-screen smoothly before calling hide
                            offsetY.animateTo(offsetY.value + 300f)
                            hideSnackbar()
                        } else {
                            // Reset position if the drag didn't cross the threshold
                            offsetY.animateTo(0f)
                        }
                    }
                },
                onDragCancel = {
                    coroutineScope.launch {
                        offsetY.animateTo(0f)
                    }
                }
            )
        }
}