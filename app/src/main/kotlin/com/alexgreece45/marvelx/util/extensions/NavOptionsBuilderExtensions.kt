package com.alexgreece45.marvelx.util.extensions

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder

/**
 * Pops all the destinations in the backstack, including the root. This extension function is
 * particularly useful when navigating to the home screen from a screen deeper in the hierarchy.
 */
fun NavOptionsBuilder.popUpToTop(navController: NavController) {
    popUpTo(navController.graph.id) {
        inclusive = true
    }
}