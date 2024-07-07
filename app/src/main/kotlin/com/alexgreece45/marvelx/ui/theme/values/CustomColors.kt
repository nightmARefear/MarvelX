package com.alexgreece45.marvelx.ui.theme.values

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * The custom colors of this theme.
 *
 * @property genderMale The color for the male gender icon.
 * @property genderFemale The color for the female gender icon.
 * @property error The color for errors.
 * @property scrim The scrim color of bottom sheets.
 */
@Immutable
data class CustomColors(
    val genderMale: Color,
    val genderFemale: Color,
    val error: Color,
    val scrim: Color
)

/**
 * The custom light colors.
 */
internal fun customLightColors() = CustomColors(
    genderMale = Color(0xFF2196F3),
    genderFemale = Color(0xFFE91E63),
    error = Color(0xFFD50000),
    scrim = Color(0xFFF1F1F1)
)

/**
 * The custom dark colors.
 */
internal fun customDarkColors() = CustomColors(
    genderMale = Color(0xFF2196F3),
    genderFemale = Color(0xFFE91E63),
    error = Color(0xFFD50000),
    scrim = Color(0xFF484848)
)

internal val LocalCustomColors = staticCompositionLocalOf { customLightColors() }

val MaterialTheme.customColors: CustomColors
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current
