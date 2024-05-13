package com.alexgreece45.marvelx.ui.navigation.router

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.alexgreece45.marvelx.ui.navigation.NavGraph
import com.alexgreece45.marvelx.ui.navigation.Screen
import com.alexgreece45.marvelx.util.extensions.popUpToTop

interface RouterHost {
    /**
     * Retrieves the root [NavHostController].
     */
    fun getNavController(): NavHostController

    /**
     * Navigates the user to the `Home Screen`, and pops all the screens from the backstack.
     *
     * ### Note: Use this function with caution.
     */
    fun logout()

    /**
     * Initializes the navigation graph.
     */
    @Composable
    fun StartFlow()
}

class RouterHostImpl : RouterHost {

    private lateinit var navController: NavHostController
    override fun getNavController(): NavHostController = navController

    override fun logout() {
        navController.navigate(Screen.RegisterScreens.HomeScreen.route) {
            popUpToTop(navController)
        }
    }

    @Composable
    override fun StartFlow() {
        navController = rememberNavController()
        NavGraph(navController = navController)
    }
}