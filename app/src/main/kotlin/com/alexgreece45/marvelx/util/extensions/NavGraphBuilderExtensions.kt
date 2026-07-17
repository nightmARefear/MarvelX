package com.alexgreece45.marvelx.util.extensions

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.alexgreece45.marvelx.ui.navigation.Screen

/**
 * Wrapper extension function providing custom route handling.
 *
 * @param screen The screen data object of this route.
 * @param enterTransition The enter transition to apply to this route when navigating to it.
 * @param exitTransition The exit transition to apply to this route when exiting from it.
 * @param popEnterTransition The enter transition to apply to this route when popping back to it.
 * @param popExitTransition The exit transition to apply to this route when popping out of it.
 * @param content The composable content of this route.
 */
fun NavGraphBuilder.composable(
    screen: Screen,
    enterTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? = null,
    exitTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? = null,
    popEnterTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        enterTransition,
    popExitTransition: (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        exitTransition,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) =
    composable(
        route = screen.route,
        arguments = screen.navigationArguments,
        deepLinks = screen.deepLinks,
        enterTransition = enterTransition,
        exitTransition = exitTransition,
        popEnterTransition = popEnterTransition,
        popExitTransition = popExitTransition,
        content = content
    )
