package com.alexgreece45.marvelx.util.extensions

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
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.util.THROTTLE_CLICKABLE_DEFAULT_DELAY
import com.google.accompanist.placeholder.PlaceholderDefaults
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow

fun Modifier.shimmer(shouldShimmer: Boolean = false) = composed {
    if (!shouldShimmer)
        this
    else
        this.placeholder(
            visible = true,
            color = PlaceholderDefaults.color(),
            highlight = PlaceholderHighlight.shimmer(
                highlightColor = PlaceholderDefaults.shimmerHighlightColor()
            )
        )
}

data class ClickState(
    val event: () -> Unit,
    var currentTimeInMillis: Long
)

fun Modifier.throttledClickableIcon(
    onClick: () -> Unit,
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