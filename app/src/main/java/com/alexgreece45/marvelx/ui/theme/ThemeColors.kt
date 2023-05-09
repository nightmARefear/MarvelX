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
            primary = colorResource(id = R.color.primary),
            primaryVariant = colorResource(id = R.color.primary_light),
            onPrimary = colorResource(id = R.color.on_primary),
            secondary = colorResource(id = R.color.secondary),
            secondaryVariant = colorResource(id = R.color.secondary_light),
            onSecondary = colorResource(id = R.color.on_secondary),
            background = colorResource(id = R.color.background),
            onBackground = colorResource(id = R.color.on_background),
            surface = colorResource(id = R.color.surface),
            onSurface = colorResource(id = R.color.on_surface),
            error = colorResource(id = R.color.error),
            onError = colorResource(id = R.color.on_error),
            success = colorResource(id = R.color.success),
            onSuccess = colorResource(id = R.color.on_success)
        ),
        dark = ColorsTemplate(
            primary = colorResource(id = R.color.primary),
            primaryVariant = colorResource(id = R.color.primary_light),
            onPrimary = colorResource(id = R.color.on_primary),
            secondary = colorResource(id = R.color.secondary),
            secondaryVariant = colorResource(id = R.color.secondary_light),
            onSecondary = colorResource(id = R.color.on_secondary),
            background = colorResource(id = R.color.background),
            onBackground = colorResource(id = R.color.on_background),
            surface = colorResource(id = R.color.surface),
            onSurface = colorResource(id = R.color.on_surface),
            error = colorResource(id = R.color.error),
            onError = colorResource(id = R.color.on_error),
            success = colorResource(id = R.color.success),
            onSuccess = colorResource(id = R.color.on_success)
        )
    )
}

@Composable
fun Colors.colorBlack(): Color {
    return Color(0xFF000000)
}