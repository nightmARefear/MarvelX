package com.alexgreece45.marvelx.ui_theme.theme.values

import androidx.compose.material.Button
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import com.alexgreece45.marvelx.ui_theme.theme.templates.ColorsTemplate
import com.alexgreece45.marvelx.ui_theme.theme.templates.ThemeColorsTemplate

@Composable
internal fun themeColors(darkTheme: Boolean): ThemeColorsTemplate {
    return ThemeColorsTemplate(
        darkTheme = darkTheme,
        light = ColorsTemplate(
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

/**
 * The Material color system contains pairs of colors that are typically used for the background and
 * content color inside a component. For example, a [Button] typically uses `primary` for its
 * background, and `onPrimary` for the color of its content (usually text or iconography).
 *
 * This function tries to match the provided [contentColor] to a 'content' color in this
 * [Colors], and then will return the corresponding color used for background. For example, when
 * [contentColor] is [Colors.primary], this will return [Colors.primary].
 *
 * If [contentColor] does not match a background color in the theme, this will return [Color.Unspecified].
 *
 * @return the matching background color for [contentColor]. If [contentColor] is not present in
 *   the theme's [Colors], then returns [Color.Unspecified].
 * @see backgroundColorFor
 */
fun Colors.backgroundColorFor(contentColor: Color): Color {
    return when (contentColor) {
        onPrimary -> primary
        onPrimary -> primaryVariant
        onSecondary -> secondary
        onSecondary -> secondaryVariant
        onBackground -> background
        onSurface -> surface
        onError -> error
        else -> Color.Unspecified
    }
}

/**
 * The Material color system contains pairs of colors that are typically used for the background and
 * content color inside a component. For example, a [Button] typically uses `primary` for its
 * background, and `onPrimary` for the color of its content (usually text or iconography).
 *
 * This function tries to match the provided [contentColor] to a 'content' color in this
 * [Colors], and then will return the corresponding color used for background. For example, when
 * [contentColor] is [Colors.primary], this will return [Colors.primary].
 *
 * If [contentColor] does not match a background color in the theme, this will return [Color.Unspecified].
 *
 * @return the matching background color for [contentColor]. If [contentColor] is not present in
 *   the theme's [Colors], then returns [Color.Unspecified].
 * @see Colors.backgroundColorFor
 */
@Composable
@ReadOnlyComposable
fun backgroundColorFor(contentColor: Color) =
    MaterialTheme.colors.backgroundColorFor(contentColor).takeOrElse { Color.Unspecified }

/**
 * Retrieves the error color.
 */
fun getErrorColor(): Color = Color(0xFFD50000)