package com.alexgreece45.marvelx.ui_theme.theme.util

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * Contains utility functions that change the colors of the status and navigation bars.
 */
object SystemBarsThemeUtil {
    /**
     * Enables edge-to-edge display and changes the system bars color in real-time based on whether
     * the app is in dark mode. The default applied colors are [Color.Transparent] which means that
     * each screen provides its own colors for the system bars in the following way:
     *
     * - For the status bar, if the `TopAppBar` exists then its background color is applied,
     * otherwise the background color of the root [Composable] of the particular screen.
     *
     * - For the navigation bar, if the `BottomBar` exists then its background color is applied,
     * otherwise the background color of the root [Composable] of the particular screen.
     *
     * If you want to provide your own colors, set [lightSystemBarColors] and/or
     * [darkSystemBarColors].
     *
     * @param activity The [ComponentActivity] to apply edge-to-edge functionality and system bars
     * colors to.
     * @param isDarkTheme Whether the theme is dark.
     * @param lightSystemBarColors The light system bar colors.
     * @param darkSystemBarColors The dark system bar colors.
     */
    @Composable
    fun ChangeSystemBarsTheme(
        activity: ComponentActivity,
        isDarkTheme: Boolean = isSystemInDarkTheme(),
        lightSystemBarColors: LightSystemBarColors = SystemBarsThemeUtilDefaults.LightSystemBarColors,
        darkSystemBarColors: DarkSystemBarColors = SystemBarsThemeUtilDefaults.DarkSystemBarColors
    ) {
        LaunchedEffect(isDarkTheme) {
            if (isDarkTheme) {
                applyDarkSystemBarsTheme(
                    activity = activity,
                    darkSystemBarColors = darkSystemBarColors
                )
            } else {
                applyLightSystemBarsTheme(
                    activity = activity,
                    lightSystemBarColors = lightSystemBarColors
                )
            }
        }
    }

    /**
     * Enables edge-to-edge display and applies light system bar colors. By default, all the values
     * are [Color.Transparent] which means that:
     *
     * - For the status bar, if the `TopAppBar` exists then its background color is applied,
     * otherwise the background color of the root [Composable] of the particular screen.
     *
     * - For the navigation bar, if the `BottomBar` exists then its background color is applied,
     * otherwise the background color of the root [Composable] of the particular screen.
     *
     * If you want to provide your own colors, set [lightSystemBarColors].
     *
     * @param activity The [ComponentActivity] to apply edge-to-edge functionality and light system
     * bars colors to.
     * @param lightSystemBarColors The light system bar colors.
     */
    fun applyLightSystemBarsTheme(
        activity: ComponentActivity,
        lightSystemBarColors: LightSystemBarColors = SystemBarsThemeUtilDefaults.LightSystemBarColors
    ) {
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                scrim = lightSystemBarColors.statusBarScrimColor.toArgb(),
                darkScrim = lightSystemBarColors.statusBarDarkScrimColor.toArgb(),
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = lightSystemBarColors.navigationBarScrimColor.toArgb(),
                darkScrim = lightSystemBarColors.navigationBarDarkScrimColor.toArgb(),
            )
        )
    }

    /**
     * Enables edge-to-edge display and applies dark system bar colors. By default all the values
     * are [Color.Transparent] which means that:
     *
     * - For the status bar, if the `TopAppBar` exists then its background color is applied,
     * otherwise the background color of the root [Composable] of the particular screen.
     *
     * - For the navigation bar, if the `BottomBar` exists then its background color is applied,
     * otherwise the background color of the root [Composable] of the particular screen.
     *
     * If you want to provide your own colors, set [darkSystemBarColors].
     *
     * @param activity The [ComponentActivity] to apply edge-to-edge functionality and dark system
     * bars colors to.
     * @param darkSystemBarColors The dark system bar colors.
     */
    fun applyDarkSystemBarsTheme(
        activity: ComponentActivity,
        darkSystemBarColors: DarkSystemBarColors = SystemBarsThemeUtilDefaults.DarkSystemBarColors,
    ) {
        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = darkSystemBarColors.statusBarScrimColor.toArgb(),
            ),
            navigationBarStyle = SystemBarStyle.dark(
                scrim = darkSystemBarColors.navigationBarScrimColor.toArgb(),
            )
        )
    }
}

/**
 * Contains the light colors used for the system bars.
 *
 * @param statusBarScrimColor The scrim color for the status bar to be used for the background.
 * It is expected to be light for the contrast against the dark system icons.
 * @param statusBarDarkScrimColor The scrim color for the status bar to be used for the
 * background on devices where the system icon color is always light. It is expected to be dark.
 * @param navigationBarScrimColor The scrim color for the navigation bar to be used for the
 * background. It is expected to be light for the contrast against the dark system icons.
 * @param navigationBarDarkScrimColor The scrim color for the navigation bar to be used for the
 * background on devices where the system icon color is always light. It is expected to be dark.
 */
@Immutable
data class LightSystemBarColors(
    val statusBarScrimColor: Color,
    val statusBarDarkScrimColor: Color,
    val navigationBarScrimColor: Color,
    val navigationBarDarkScrimColor: Color
)

/**
 * Contains the dark colors used for the system bars.
 *
 * @param statusBarScrimColor The scrim color for the status bar to be used for the background.
 * It is expected to be dark for the contrast against the light system icons.
 * @param navigationBarScrimColor The scrim color for the navigation bar to be used for the
 * background. It is expected to be dark for the contrast against the light system icons.
 */
@Immutable
data class DarkSystemBarColors(
    val statusBarScrimColor: Color,
    val navigationBarScrimColor: Color
)

/**
 * Contains all the default values of the [SystemBarsThemeUtil]'s functions.
 */
object SystemBarsThemeUtilDefaults {
    /**
     * The default light system bar colors.
     */
    val LightSystemBarColors = LightSystemBarColors(
        statusBarScrimColor = Color.Transparent,
        statusBarDarkScrimColor = Color.Transparent,
        navigationBarScrimColor = Color.Transparent,
        navigationBarDarkScrimColor = Color.Transparent
    )

    /**
     * The default dark system bar colors.
     */
    val DarkSystemBarColors = DarkSystemBarColors(
        statusBarScrimColor = Color.Transparent,
        navigationBarScrimColor = Color.Transparent
    )
}