package com.alexgreece45.marvelx.ui.theme

import androidx.compose.material.Colors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.alexgreece45.marvelx.R

@Composable
fun themeColors(darkTheme: Boolean): ThemeColorsTemplate {
    return ThemeColorsTemplate(
        darkTheme = darkTheme,
        light = ColorsTemplate(
            primary = Color(0xFFEC1D24),
            primaryVariant = Color(0xFFEC1D24),
            onPrimary = Color(0xFFFFFFFF),
            secondary = Color(0xFFDF9252),
            secondaryVariant = Color(0xFF000000),
            onSecondary = Color(0xFF000000),
            background = Color(0xFFF3E2E2),
            onBackground = Color(0xFF000000),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF000000),
            error = Color(0xFFD50000),
            onError = Color(0xFF000000),
            success = Color(0xFF009900),
            onSuccess = Color(0xFFFFFFFF)
        ),
        dark = ColorsTemplate(
            primary = Color(0xFFFFFFFF),
            primaryVariant = Color(0xFFE62429),
            onPrimary = Color(0xFFDD7C7C),
            secondary = Color(0xFFDF9252),
            secondaryVariant = Color(0xFF000000),
            onSecondary = Color(0xFF000000),
            background = Color(0xFF0C0B0B),
            onBackground = Color(0xFFFFFFFF),
            surface = Color(0xFF121212),
            onSurface = Color(0xFFFFFFFF),
            error = Color(0xFFD50000),
            onError = Color(0xFF000000),
            success = Color(0xFF009900),
            onSuccess = Color(0xFFFFFFFF)
        )
    )
}

@Composable
fun Colors.colorBlack(): Color {
    return Color(0xFF000000)
}