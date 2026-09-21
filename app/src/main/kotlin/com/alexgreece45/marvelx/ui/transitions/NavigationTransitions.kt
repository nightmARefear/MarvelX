package com.alexgreece45.marvelx.ui.transitions

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry

/**
 * The single source of truth for screen-to-screen navigation animations.
 *
 * Forward and back navigation are mirror images of each other: the incoming screen always travels
 * in the opposite direction to the outgoing one, otherwise both slide the same way and read as a
 * jump rather than a push. The screen being left behind only moves a fraction of the width, which
 * is what produces the layered "push" effect instead of two screens racing each other.
 */
object NavigationTransitions {

    private const val DURATION_MILLIS = 320
    private const val FADE_DURATION_MILLIS = 220

    /**
     * How far the backgrounded screen travels, as a fraction of the full width. Keeping it well
     * below 1 leaves it partially visible so the new screen reads as sliding on top of it.
     */
    private const val PARALLAX_FACTOR = 4

    private val easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    private fun <T> animationSpec(durationMillis: Int = DURATION_MILLIS) =
        tween<T>(durationMillis = durationMillis, easing = easing)

    private val fadeInSpec = fadeIn(animationSpec = animationSpec(FADE_DURATION_MILLIS))
    private val fadeOutSpec = fadeOut(animationSpec = animationSpec(FADE_DURATION_MILLIS))

    // Horizontal (default push navigation).

    /**
     * New screen slides in from the right edge.
     */
    val enter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(animationSpec = animationSpec()) { fullWidth -> fullWidth } + fadeInSpec
    }

    /**
     * Previous screen drifts left, staying beneath the incoming one.
     */
    val exit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(animationSpec = animationSpec()) { fullWidth ->
            -fullWidth / PARALLAX_FACTOR
        } + fadeOutSpec
    }

    /**
     * Returning screen drifts back in from the left, reversing [exit].
     */
    val popEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInHorizontally(animationSpec = animationSpec()) { fullWidth ->
            -fullWidth / PARALLAX_FACTOR
        } + fadeInSpec
    }

    /**
     * Screen being dismissed slides back out to the right, reversing [enter].
     */
    val popExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutHorizontally(animationSpec = animationSpec()) { fullWidth -> fullWidth } + fadeOutSpec
    }

    // Vertical (modal-style screens presented from the bottom).

    val modalEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideInVertically(animationSpec = animationSpec()) { fullHeight -> fullHeight } + fadeInSpec
    }

    /**
     * A modal covers the screen underneath rather than displacing it, so the backgrounded screen
     * only fades slightly instead of moving.
     */
    val modalExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOutSpec
    }

    val modalPopEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeInSpec
    }

    val modalPopExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutVertically(animationSpec = animationSpec()) { fullHeight -> fullHeight } + fadeOutSpec
    }

    // Fade only, for swaps where no spatial relationship exists (e.g. bottom navigation tabs).

    val fadeEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        fadeInSpec
    }

    val fadeExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        fadeOutSpec
    }
}
