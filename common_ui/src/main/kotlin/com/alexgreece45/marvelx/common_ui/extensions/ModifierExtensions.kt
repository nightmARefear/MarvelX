package com.alexgreece45.marvelx.common_ui.extensions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import kotlin.concurrent.atomics.ExperimentalAtomicApi

@OptIn(ExperimentalAtomicApi::class)
fun Modifier.clickableNoRipple(
    enabled: Boolean = true,
    rippleShape: Shape? = null,
    role: Role? = null,
    onClick: () -> Unit
): Modifier = composed {
    val clipModifier = if (rippleShape != null) {
        Modifier.clip(rippleShape)
    } else {
        Modifier
    }

    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = enabled,
        role = role,
        onClick = onClick
    ).then(clipModifier)
}