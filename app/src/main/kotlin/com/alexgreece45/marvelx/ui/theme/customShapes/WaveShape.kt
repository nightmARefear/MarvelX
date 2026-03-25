package com.alexgreece45.marvelx.ui.theme.customShapes

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

/**
 * Defines a wave-like shape.
 */
class WaveShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(0f, size.height * 0.2f)
            quadraticTo(
                x1 = size.width * 0.25f, size.height * 0.4f,
                x2 = size.width * 0.5f, size.height * 0.2f
            )
            quadraticTo(
                x1 = size.width * 0.75f, 0f,
                x2 = size.width, size.height * 0.2f
            )
            lineTo(size.width, 0f)
            close()
        }
        return Outline.Generic(path)
    }
}
