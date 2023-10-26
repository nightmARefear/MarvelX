package com.alexgreece45.marvelx.util.extensions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.IconButton
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
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
