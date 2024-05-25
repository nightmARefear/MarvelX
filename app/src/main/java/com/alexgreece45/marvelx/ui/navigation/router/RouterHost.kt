package com.alexgreece45.marvelx.ui.navigation.router

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alexgreece45.marvelx.ui.navigation.NavGraph
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.util.extensions.popUpToTop

interface RouterHost {
    /**
     * Initializes the navigation graph.
     */
    @Composable
    fun StartFlow()

    /**
     * Navigates the user to the `Home Screen`, and pops all the screens from the backstack.
     *
     * ### Note: Use this function with caution.
     */
    fun logout()

    /**
     * Returns the root [NavHostController].
     */
    fun getNavController(): NavHostController

    /**
     * Returns `true` if the current route is included in the auto-logout mechanism, otherwise
     * `false`.
     */
    fun isCurrentRouteIncludedInAutoLogout(): Boolean

    /**
     * Indicates whether the navigation flow has started. It has an initial value of `false`.
     */
    val flowStarted: Boolean
}

class RouterHostImpl : RouterHost {

    /**
     * Indicates whether the navigation flow has started. It has an initial value of `false`.
     */
    private var _flowStarted: Boolean = false

    override val flowStarted: Boolean get() = _flowStarted

    /**
     * The root [NavHostController] of this application.
     */
    private lateinit var navController: NavHostController

    @Composable
    override fun StartFlow() {
        // Ensures that the navigation graph is only set once.
        if (_flowStarted.not()) {
            navController = rememberNavController()
            NavGraph(navController = navController)
            _flowStarted = true
        }
    }

    override fun logout() {
        navController.navigate(Screen.HomeScreen.route) {
            popUpToTop(navController)
        }
    }

    override fun getNavController(): NavHostController = navController

    override fun isCurrentRouteIncludedInAutoLogout(): Boolean =
        Screen.fromRoute(navController.currentDestination?.route.orEmpty())?.isIncludedInAutoLogout
            ?: false
}