package com.alexgreece45.marvelx.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.Shapes

abstract class ThemeShapesTemplate {

    val shapes: Shapes
        get() {
            return Shapes(
                small = smallShape,
                medium = mediumShape,
                large = largeShape
            )
        }

    abstract val smallShape: CornerBasedShape
    abstract val mediumShape: CornerBasedShape
    abstract val largeShape: CornerBasedShape
}