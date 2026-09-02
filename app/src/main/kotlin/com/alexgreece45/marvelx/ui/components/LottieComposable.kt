package com.alexgreece45.marvelx.ui.components

import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.alexgreece45.marvelx.ui_theme.theme.values.LocalCustomColors
import com.alexgreece45.marvelx.util.extensions.clickableNoRipple

/**
 * A Composable function that displays a Lottie animation with optional subtitle content.
 *
 * @param resourceId            The raw resource ID of the Lottie animation JSON file.
 * @param size                  The size of the Lottie animation.
 * @param modifier              The modifier to be applied to the container Box.
 * @param iterations            The number of iterations the animation should run, defaults to
 * [LottieConstants.IterateForever].
 * @param backgroundColor       The background color of the container, defaults to MaterialTheme
 * background.
 * @param clipLottie            Whether this lottie should be clipped to the specified size. It has
 * the default value of `false`. This setting should be used with caution!
 * @param verticalArrangement    The vertical arrangement of the content inside the Column, defaults
 * to [Arrangement.Center].
 * @param horizontalAlignment   The horizontal alignment of the content inside the Column, defaults
 * to [Alignment.CenterHorizontally].
 * @param subtitleContent An optional Composable function to display subtitle content below the
 * animation.
 */
@Composable
fun LottieComposable(
    @RawRes resourceId: Int,
    size: Dp,
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever,
    backgroundColor: Color? = null,
    clipLottie: Boolean = false,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    subtitleContent: (@Composable () -> Unit)? = null,
) {
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(
            resId = resourceId
        )
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations
    )

    val clipModifier = if (clipLottie) Modifier else Modifier.fillMaxSize()

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .then(clipModifier)
//                .let{
//                    if (backgroundColor != null) {
//                        Modifier.background(backgroundColor)
//                    } else {
//                        Modifier
//                    }
//                }
                .background(backgroundColor ?: MaterialTheme.colors.background)
                .clickableNoRipple(onClick = {}),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(size)
            )

            subtitleContent?.invoke()
        }
    }
}
