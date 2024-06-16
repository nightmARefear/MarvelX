package com.alexgreece45.marvelx.ui.theme

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.alexgreece45.marvelx.ui.theme.customShapes.CustomShapes

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

/**
 * Retrieves the current value of [LocalCustomShapes] from the composition.
 * This provides access to the custom defined shapes for the current composition context.
 */
val MaterialTheme.customShapes: CustomShapes
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomShapes.current