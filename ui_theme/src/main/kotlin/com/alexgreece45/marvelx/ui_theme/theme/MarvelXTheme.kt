package com.alexgreece45.marvelx.ui_theme.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.alexgreece45.marvelx.ui_theme.theme.values.LocalCustomColors
import com.alexgreece45.marvelx.ui_theme.theme.values.LocalCustomShapes
import com.alexgreece45.marvelx.ui_theme.theme.values.ThemeShapes
import com.alexgreece45.marvelx.ui_theme.theme.values.ThemeTypography
import com.alexgreece45.marvelx.ui_theme.theme.values.customDarkColors
import com.alexgreece45.marvelx.ui_theme.theme.values.customLightColors
import com.alexgreece45.marvelx.ui_theme.theme.values.customShapes
import com.alexgreece45.marvelx.ui_theme.theme.values.themeColors
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme

@Composable
fun MarvelXTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    val colors = themeColors(darkTheme).colors
    val customColors = if (darkTheme) customDarkColors() else customLightColors()
    val shapes = ThemeShapes().shapes
    val typography = ThemeTypography().typo

    val shimmerTheme = defaultShimmerTheme.copy()

    CompositionLocalProvider(
        LocalCustomColors provides customColors,
        LocalCustomShapes provides customShapes(),
        LocalShimmerTheme provides shimmerTheme
    ) {
        MaterialTheme(
            colors = colors,
            typography = typography,
            shapes = shapes,
            content = content
        )
    }
}