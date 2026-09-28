package com.alexgreece45.marvelx.util.extensions

import android.view.View
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.IconButton
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.util.THROTTLE_CLICKABLE_DEFAULT_DELAY
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * Provides protection against clicking an Icon multiple times within a short period of time,
 * and against UI overlay attacks from other applications.
 *
 * This [Modifier] allows this UI element to be clicked at the most once per the given [delay].
 *
 * @param view          The parent [View] of this UI element.
 * @param delay         The delay between click events.
 * @param enabled       Controls the enabled state. When `false`, [onClick], and this modifier will
 * appear disabled for accessibility services.
 * @param onClickLabel  Semantic / accessibility label for the [onClick] action.
 * @param onClick       The action(s) to be invoked when a UI element is clicked.
 */
fun Modifier.throttledClickableIcon(
    view: View,
    enabled: Boolean = true,
    onClickLabel: String? = null,
    delay: Long = THROTTLE_CLICKABLE_DEFAULT_DELAY,
    onClick: () -> Unit
) = throttledClickable(
    view = view,
    enabled = enabled,
    role = Role.Button,
    onClickLabel = onClickLabel,
    delay = delay,
    isIcon = true,
    showRipple = false,
    onClick = onClick,
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
 * @param isIcon        Whether this [Modifier] will be applied to an [Icon].
 * @param showRipple    Whether the ripple effect should be visible. It has the default value of
 * `true`.
 * @param rippleShape   The shape of the ripple. If `null` is provided, then the theme's default
 * shape is used instead. __Note:__ for the ripple shape to appear, make sure that [showRipple] is
 * `true`.
 * @param onClick       The action(s) to be invoked when a UI element is clicked.
 */
@OptIn(ExperimentalAtomicApi::class)
fun Modifier.throttledClickable(
    view: View,
    enabled: Boolean = true,
    role: Role? = null,
    onClickLabel: String? = null,
    delay: Long = THROTTLE_CLICKABLE_DEFAULT_DELAY,
    isIcon: Boolean = false,
    showRipple: Boolean = true,
    rippleShape: Shape? = null,
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
    val lastClickedMillis = remember { AtomicLong(0) }

    var modifier: Modifier = Modifier
    if (isIcon) {
        modifier = Modifier.minimumInteractiveComponentSize()
    }

    // Clip content to the provided shape.
    val clipModifier = if (rippleShape != null) {
        Modifier.clip(rippleShape)
    } else {
        Modifier
    }

    /**
     * Default radius of an unbounded ripple of an [IconButton] as defined in [IconButton].
     */
    val RippleRadius = 24.dp
    modifier
        .then(clipModifier)
        .filterTouchesWhenObscured(view)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = if (showRipple) LocalIndication.current else null,
            enabled = enabled,
            onClickLabel = onClickLabel,
            role = if (isIcon) role ?: Role.Button else role,
            onClick = {
                val currentTimeMillis = System.currentTimeMillis()
                if (currentTimeMillis - lastClickedMillis.load() > delay) {
                    lastClickedMillis.store(currentTimeMillis)
                    onClick()
                }
            }
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

/**
 * Applies a border and padding to an image Modifier.
 *
 * @param borderWidth   The width of the border to apply.
 * @param borderColor   The color of the border to apply.
 * @param shape         The shape to apply.
 *
 * @return A Modifier that applies a circular border and padding to an image.
 */
fun Modifier.imageWithBorder(borderWidth: Dp, borderColor: Color, shape: Shape): Modifier =
    this
        .border(BorderStroke(width = borderWidth, color = borderColor), shape)
        .padding(borderWidth)
        .clip(shape)

/**
 * Applies a circular border and padding to an image Modifier.
 *
 * @param borderWidth The width of the border to apply.
 * @param borderColor The color of the border to apply.
 *
 * @return A Modifier that applies a circular border and padding to an image.
 */
fun Modifier.circularImageWithBorder(borderWidth: Dp, borderColor: Color): Modifier =
    this
        .imageWithBorder(borderWidth, borderColor, CircleShape)
