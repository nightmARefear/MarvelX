package com.alexgreece45.marvelx.ui.components.model

import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.DefaultShadowColor
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Data for the [shadow] modifier.
 *
 * @param elevation     The elevation for the shadow in pixels
 * @param shape         Defines a shape of the physical object
 * @param clip          When active, the content drawing clips to the shape.
 * @param ambientColor  The ambient color.
 * @param spotColor     The spot color.
 */
data class ShadowData(
    val elevation: Dp,
    val shape: Shape = RectangleShape,
    val clip: Boolean = elevation > 0.dp,
    val ambientColor: Color = DefaultShadowColor,
    val spotColor: Color = DefaultShadowColor,
)