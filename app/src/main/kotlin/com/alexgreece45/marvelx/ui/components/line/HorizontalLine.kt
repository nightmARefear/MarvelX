package com.alexgreece45.marvelx.ui.components.line

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Draws a horizontal line based on the specified arguments.
 *
 * @param color         The color of this line.
 * @param modifier      The modifier to apply.
 * @param strokeWidth   The stroke width to apply to this line in [Dp]. It has a default value of
 * `1.`[dp].
 * @param cap           The treatment to apply to the ends of this line. It has a default value of
 * [StrokeCap.Round].
 */
@Composable
fun HorizontalLine(
    color: Color,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 1.dp,
    cap: StrokeCap = StrokeCap.Round,
) {
    Canvas(modifier) {
        drawLine(
            color = color,
            start = Offset.Zero,
            end = Offset(x = size.width, y = size.height),
            strokeWidth = strokeWidth.toPx(),
            cap = cap
        )
    }
}