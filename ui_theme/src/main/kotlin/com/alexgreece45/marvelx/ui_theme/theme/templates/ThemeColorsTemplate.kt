package com.alexgreece45.marvelx.ui_theme.theme.templates

import androidx.compose.material.Colors
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

data class ThemeColorsTemplate(
    private val darkTheme: Boolean,
    val light: ColorsTemplate,
    val dark: ColorsTemplate
) {
    val colors: Colors
        get() {
            return when (darkTheme) {
                true -> dark.toColors(true)
                false -> light.toColors(false)
            }
        }
}

@Immutable
data class ColorsTemplate(
    val primary: Color,
    val primaryVariant: Color,
    val onPrimary: Color,

    val secondary: Color,
    val secondaryVariant: Color,
    val onSecondary: Color,

    val background: Color,
    val onBackground: Color,

    val surface: Color,
    val onSurface: Color,

    val error: Color,
    val onError: Color,

    val success: Color,
    val onSuccess: Color
)

fun ColorsTemplate.toColors(dark: Boolean): Colors {
    return Colors(
        primary = primary,
        primaryVariant = primaryVariant,
        onPrimary = onPrimary,

        secondary = secondary,
        secondaryVariant = secondaryVariant,
        onSecondary = onSecondary,

        background = background,
        onBackground = onBackground,

        surface = surface,
        onSurface = onSurface,

        error = error,
        onError = onError,

        isLight = !dark
    )
}