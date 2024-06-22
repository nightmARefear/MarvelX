package com.alexgreece45.marvelx.util.extensions

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.ui.navigation.Screen

/**
 * Wrapper extension function providing custom route handling.
 *
 * @param screen    The screen data object of this `Composable`.
 * @param content   The content of this `Composable`.
 */
fun NavGraphBuilder.composable(
    screen: Screen,
    content: @Composable (NavBackStackEntry) -> Unit
) {
    composable(
        route = screen.route,
        arguments = screen.navigationArguments,
        deepLinks = screen.deepLinks
    ) {
        content(it)
    }
}