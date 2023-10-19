package com.alexgreece45.marvelx.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.alexgreece45.marvelx.ui.screens.otpScreen.OtpScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.homeScreen.HomeScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.logInScreen.LogInScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.signUpScreen.SignUpScreen

fun NavGraphBuilder.RegisterNavGraph(
    navController: NavController,
    setUserId: (Int) -> Unit
) =
    navigation(
        route = Screen.RegisterScreens.getNestedGraphRootRoute(),
        startDestination = Screen.RegisterScreens.getStartDestination()
    ) {
        composable(Screen.RegisterScreens.HomeScreen) {
            HomeScreen(
                navController = navController,
                setUserId = { userId ->
                    setUserId.invoke(userId)
                }
            )
        }

        composable(Screen.RegisterScreens.LogInScreen) {
            LogInScreen(
                navController = navController,
                setUserId = { userId ->
                    setUserId.invoke(userId)
                }
            )
        }

        composable(Screen.RegisterScreens.SignUpScreen) {
            SignUpScreen(navController = navController)
        }

        composable(Screen.OtpScreen) { navBackStackEntry ->
            OtpScreen(
                navController = navController,
                userId = navBackStackEntry.arguments?.getInt(NavItem.User.key) ?: -1,
                setUserId = { userId ->
                    setUserId.invoke(userId)
                }
            )
        }
    }