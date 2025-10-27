package com.alexgreece45.marvelx.ui.theme.values

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import com.alexgreece45.marvelx.ui.theme.templates.ColorsTemplate
import com.alexgreece45.marvelx.ui.theme.templates.ThemeColorsTemplate

@Composable
fun themeColors(darkTheme: Boolean): ThemeColorsTemplate {
    return ThemeColorsTemplate(
        darkTheme = darkTheme,
        light = ColorsTemplate(
            primary = Color(0xFFEC1D24),
            primaryVariant = Color(0xFF921318),
            onPrimary = Color(0xFFFFFFFF),
            secondary = Color(0xFFEC1D24),
            secondaryVariant = Color(0xFF000000),
            onSecondary = Color(0xFF000000),
            background = Color(0xFFFFFFFF),
            onBackground = Color(0xFF000000),
            surface = Color(0xFFF1EDED),
            onSurface = Color(0xFF000000),
            error = Color(0xFFD50000),
            onError = Color(0xFF000000),
            success = Color(0xFF00D500),
            onSuccess = Color(0xFFFFFFFF)
        ),
        dark = ColorsTemplate(
            primary = Color(0xFFFFFFFF),
            primaryVariant = Color(0xFFCECECE),
            onPrimary = Color(0xFF1B1314),
            secondary = Color(0xFFBC0000),
            secondaryVariant = Color(0xFFA20000),
            onSecondary = Color(0xFFFAE5E5),
            background = Color(0xFF222222),
            onBackground = Color(0xFFFFFFFF),
            surface = Color(0xFF3A3939),
            onSurface = Color(0xFFFFFFFF),
            error = Color(0xFFD50000),
            onError = Color(0xFF000000),
            success = Color(0xFF00D500),
            onSuccess = Color(0xFFFFFFFF)
        )
    )
}

fun Colors.colorSuccess(): Color = Color(0xFF009900)

/**
 * Retrieves the error color.
 */
fun getErrorColor(): Color = Color(0xFFD50000)

fun Colors.colorBlack(): Color {
    return Color(0xFF000000)
}
