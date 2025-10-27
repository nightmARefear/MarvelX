package com.alexgreece45.marvelx.ui.theme.templates

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.alexgreece45.marvelx.ui.theme.values.CustomShapes
import com.alexgreece45.marvelx.ui.theme.values.LocalCustomShapes

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
