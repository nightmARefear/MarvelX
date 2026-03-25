package com.alexgreece45.marvelx.ui.theme.values

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.alexgreece45.marvelx.ui.theme.customShapes.WaveShape

/**
 * A collection of custom [Shape] implementations for use in Jetpack Compose layouts.
 */
@Immutable
data class CustomShapes(
    /**
     * A [Shape] that represents a wave.
     */
    val waveShape: Shape,
    val chipCornerShape: RoundedCornerShape,
)

/**
 * The custom shapes.
 */
internal fun customShapes() = CustomShapes(
    waveShape = WaveShape(),
    chipCornerShape = RoundedCornerShape(6.dp)
)

internal val LocalCustomShapes = staticCompositionLocalOf { customShapes() }

/**
 * Retrieves the current value of [LocalCustomShapes] from the composition.
 * This provides access to the custom defined shapes for the current composition context.
 */
val MaterialTheme.customShapes: CustomShapes
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomShapes.current
