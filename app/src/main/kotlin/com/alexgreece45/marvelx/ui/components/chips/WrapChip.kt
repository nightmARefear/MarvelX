package com.alexgreece45.marvelx.ui.components.chips

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Chip
import androidx.compose.material.ChipColors
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.util.THROTTLE_CLICKABLE_DEFAULT_DELAY
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
 * Wrapper composable function for the native [Chip] providing custom styling, and multiple-clicks
 * protection.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WrapChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    throttleClicks: Boolean = true,
    throttleDuration: Long = THROTTLE_CLICKABLE_DEFAULT_DELAY,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small.copy(CornerSize(percent = 50)),
    border: BorderStroke? = null,
    colors: ChipColors = ChipDefaults.chipColors(),
    leadingIcon: @Composable (() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit
) = WrapChipImpl(
    onClick = onClick,
    modifier = modifier,
    throttleClicks = throttleClicks,
    throttleDuration = throttleDuration,
    enabled = enabled,
    interactionSource = interactionSource,
    shape = shape,
    border = border,
    colors = colors,
    leadingIcon = leadingIcon,
    content = content
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun WrapChipImpl(
    onClick: () -> Unit,
    modifier: Modifier,
    throttleClicks: Boolean,
    throttleDuration: Long,
    enabled: Boolean,
    interactionSource: MutableInteractionSource,
    shape: Shape,
    border: BorderStroke?,
    colors: ChipColors,
    leadingIcon: @Composable (() -> Unit)?,
    content: @Composable RowScope.() -> Unit
) {
    var lastClicked by remember { mutableLongStateOf(0L) }
    val coroutineScope = rememberCoroutineScope()
    val debounceState = remember {
        MutableSharedFlow<Long>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
    }

    LaunchedEffect(Unit) {
        debounceState.collect { currentTime ->
            if (currentTime - lastClicked >= throttleDuration) {
                onClick()
                lastClicked = currentTime
            }
        }
    }

    val contentColor by colors.contentColor(enabled)
    Surface(
        onClick = {
            if (throttleClicks) {
                coroutineScope.launch {
                    debounceState.emit(System.currentTimeMillis())
                }
            } else {
                onClick()
            }
        },
        modifier = modifier.semantics { role = Role.Button },
        enabled = enabled,
        shape = shape,
        color = colors.backgroundColor(enabled).value,
        contentColor = contentColor.copy(1.0f),
        border = border,
        interactionSource = interactionSource,
    ) {
        CompositionLocalProvider(LocalContentAlpha provides contentColor.alpha) {
            ProvideTextStyle(
                value = MaterialTheme.typography.body2
            ) {
                Row(
                    Modifier
                        .defaultMinSize(
                            minHeight = ChipDefaults.MinHeight
                        )
                        .padding(horizontal = WrapChipDefaults.HorizontalPadding),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (leadingIcon != null) {
                        Spacer(Modifier.width(WrapChipDefaults.LeadingIconStartSpacing))
                        val leadingIconContentColor by colors.leadingIconContentColor(enabled)
                        CompositionLocalProvider(
                            LocalContentColor provides leadingIconContentColor,
                            LocalContentAlpha provides leadingIconContentColor.alpha,
                            content = leadingIcon
                        )
                        Spacer(Modifier.width(WrapChipDefaults.LeadingIconEndSpacing))
                    }
                    content()
                }
            }
        }
    }
}

/**
 * Contains all the default values for [WrapChip].
 */
object WrapChipDefaults {
    val HorizontalPadding = 12.dp
    val LeadingIconStartSpacing = 4.dp
    val LeadingIconEndSpacing = 8.dp
}
