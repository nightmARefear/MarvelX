package com.alexgreece45.marvelx.ui.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.tween

/**
 * The default modal bottom sheet show animation, especially useful when another bottom sheet
 * has to be displayed just right after the previous one. It provides a fast-out, linear-in tween
 * animation spec which means that it starts at rest and ends at peak velocity.
 *
 * @return  An [AnimationSpec] configured with a duration of `150` milliseconds and
 * [FastOutLinearInEasing] easing curve.
 */
fun modalBottomSheetShowAnimation(): AnimationSpec<Float> =
    tween(
        durationMillis = 150,
        easing = FastOutLinearInEasing
    )
