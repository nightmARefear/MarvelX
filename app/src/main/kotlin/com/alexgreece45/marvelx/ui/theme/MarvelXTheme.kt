package com.alexgreece45.marvelx.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.alexgreece45.marvelx.ui.theme.values.LocalCustomColors
import com.alexgreece45.marvelx.ui.theme.values.LocalCustomShapes
import com.alexgreece45.marvelx.ui.theme.values.ThemeShapes
import com.alexgreece45.marvelx.ui.theme.values.ThemeTypography
import com.alexgreece45.marvelx.ui.theme.values.colorBlack
import com.alexgreece45.marvelx.ui.theme.values.customDarkColors
import com.alexgreece45.marvelx.ui.theme.values.customLightColors
import com.alexgreece45.marvelx.ui.theme.values.customShapes
import com.alexgreece45.marvelx.ui.theme.values.themeColors
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.defaultShimmerTheme

@Composable
fun MarvelXTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    // Initialize theme colors.
    val colors = themeColors(darkTheme).colors
    val customColors = if (darkTheme) customDarkColors() else customLightColors()

    // Initialize theme shapes.
    val shapes = ThemeShapes().shapes

    // Initialize theme typography.
    val typo = ThemeTypography().typo

    // Initialize system ui controller.
    val systemUiController = rememberSystemUiController()

    // Update status bar colors.
    systemUiController.setSystemBarsColor(
        color = colors.background,
        darkIcons = !darkTheme
    )

    // Update navigation bar colors.
    systemUiController.setNavigationBarColor(
        color = if (darkTheme) colors.background else MaterialTheme.colors.colorBlack(),
        darkIcons = !darkTheme
    )

    val shimmerTheme = defaultShimmerTheme.copy()

    CompositionLocalProvider(
        LocalCustomColors provides customColors,
        LocalCustomShapes provides customShapes(),
        LocalShimmerTheme provides shimmerTheme
    ) {
        MaterialTheme(
            colors = colors,
            typography = typo,
            shapes = shapes,
            content = content
        )
    }
}
