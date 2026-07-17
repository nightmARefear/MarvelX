package com.alexgreece45.marvelx.ui.components

import androidx.compose.foundation.Image
import androidx.compose.material.ContentAlpha
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale

@Composable
fun WrapImage(
    painter: Painter,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null
) {
    val disabledColorFilter = remember {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
    }

    Image(
        painter = painter,
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = if (enabled) alpha else ContentAlpha.disabled,
        colorFilter = if (enabled) colorFilter else disabledColorFilter
    )
}