package com.alexgreece45.marvelx.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MarvelXTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {

    // Initialize theme colors.
    val colors = themeColors(darkTheme).colors

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

    MaterialTheme(
        colors = colors,
        typography = typo,
        shapes = shapes,
        content = content
    )
}