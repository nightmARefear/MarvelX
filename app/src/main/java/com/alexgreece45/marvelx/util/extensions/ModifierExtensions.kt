package com.alexgreece45.marvelx.util.extensions

import android.view.View
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.util.THROTTLE_CLICKABLE_DEFAULT_DELAY
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

data class ClickState(
    val event: () -> Unit,
    var currentTimeInMillis: Long
)

/**
 * Provides protection against clicking a UI element multiple times within a short period of time,
 * and against UI overlay attacks from other applications.
 *
 * This [Modifier] allows this UI element to be clicked at the most once per the given [delay].
 *
 * @param view          The parent [View] of this UI element.
 * @param delay         The delay between click events.
 * @param enabled       Controls the enabled state. When `false`, [onClick], and this modifier will
 * appear disabled for accessibility services.
 * @param role          The type of user interface element. This is used by the accessibility
 * services.
 * @param onClickLabel  Semantic / accessibility label for the [onClick] action.
 * @param onClick       The action(s) to be invoked when a UI element is clicked.
 */
fun Modifier.throttledClickable(
    view: View,
    delay: Long = THROTTLE_CLICKABLE_DEFAULT_DELAY,
    enabled: Boolean = true,
    role: Role? = null,
    onClickLabel: String? = null,
    onClick: () -> Unit
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    }
) {

    var lastClicked: Long by remember { mutableLongStateOf(0) }

    val debounceState = remember {
        MutableSharedFlow<ClickState>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    LaunchedEffect(Unit) {
        debounceState
            .collect {
                if (lastClicked <= 0 || (it.currentTimeInMillis - lastClicked) >= delay) {
                    it.event()
                    lastClicked = it.currentTimeInMillis
                }
            }
    }

    Modifier
        .filterTouchesWhenObscured(view)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = LocalIndication.current,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = role,
            onClick = { debounceState.tryEmit(ClickState(onClick, System.currentTimeMillis())) }
        )
}

fun Modifier.throttledClickableIcon(
    onClick: () -> Unit,
    view: View,
    delay: Long = THROTTLE_CLICKABLE_DEFAULT_DELAY,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    interactionSource: MutableInteractionSource = MutableInteractionSource()
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["onClick"] = onClick
    }
) {

    var lastClicked: Long by remember { mutableLongStateOf(0) }

    val debounceState = remember {
        MutableSharedFlow<ClickState>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    LaunchedEffect(Unit) {
        debounceState
            .collect {
                if (lastClicked <= 0 || (it.currentTimeInMillis - lastClicked) >= delay) {
                    it.event()
                    lastClicked = it.currentTimeInMillis
                }
            }
    }

    /**
     * Default radius of an unbounded ripple of an [IconButton] as defined in [IconButton].
     * */
    val RippleRadius = 24.dp
    Modifier
        .minimumInteractiveComponentSize()
        .filterTouchesWhenObscured(view)
        .clickable(
            enabled = enabled,
            onClickLabel = onClickLabel,
            onClick = { debounceState.tryEmit(ClickState(onClick, System.currentTimeMillis())) },
            role = Role.Button,
            indication = rememberRipple(bounded = false, radius = RippleRadius),
            interactionSource = remember { interactionSource }
        )
}

/**
 * Extension function for auto-filling [TextField]s.
 *
 * @param autofillTypes The list of the autofill types for this field (duplicates are removed
 * internally).
 *
 * @param onFill        The callback function that will be executed when the user selects an autofill
 * suggestion.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.autofill(
    autofillTypes: List<AutofillType>,
    onFill: (suggestionValue: String) -> Unit
) = composed {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(
        onFill = onFill,
        autofillTypes = autofillTypes.toSet().toList() // Adds only unique AutoFillTypes.
    )
    LocalAutofillTree.current += autofillNode

    this
        .onGloballyPositioned {
            autofillNode.boundingBox = it.boundsInWindow()
        }
        .onFocusChanged { focusState ->
            autofill?.run {
                if (focusState.isFocused) {
                    requestAutofillForNode(autofillNode)
                } else {
                    cancelAutofillForNode(autofillNode)
                }
            }
        }
}

fun Modifier.clickableNoRipple(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier =
    composed {
        clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
    }

/**
 * Provides protection against UI overlay attacks from other applications.
 */
fun Modifier.filterTouchesWhenObscured(view: View): Modifier = pointerInput(Unit) {
    coroutineScope {
        launch {
            awaitPointerEventScope {
                while (true) {
                    val event = awaitPointerEvent()
                    if (view.isObscured()) {
                        event.changes.forEach { it.consume() }
                    }
                }
            }
        }
    }
}