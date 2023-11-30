package com.alexgreece45.marvelx.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.alexgreece45.marvelx.ui.screens.otpScreen.OtpScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.homeScreen.HomeScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.loginScreen.LogInScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.signupScreen.SignUpScreen
import com.alexgreece45.marvelx.ui.screens.splashScreen.SplashScreen

@Suppress("FunctionName")
fun NavGraphBuilder.RegisterNavGraph(
    navController: NavController,
    setUserId: (Int) -> Unit
) =
    navigation(
        route = Screen.RegisterScreens.getNestedGraphRootRoute(),
        startDestination = Screen.RegisterScreens.getStartDestination()
    ) {
        composable(Screen.RegisterScreens.SplashScreen) {
            SplashScreen(navController = navController)
        }

        composable(Screen.RegisterScreens.HomeScreen) {
            HomeScreen(
                navController = navController,
                setUserId = { userId ->
                    setUserId.invoke(userId)
                }
            )
        }

        composable(Screen.RegisterScreens.LoginScreen) {
            LogInScreen(
                navController = navController,
                setUserId = { userId ->
                    setUserId.invoke(userId)
                }
            )
        }

        composable(Screen.RegisterScreens.SignupScreen) {
            SignUpScreen(navController = navController)
        }

        composable(Screen.OtpScreen) { navBackStackEntry ->
            OtpScreen(
                navController = navController,
                userId = navBackStackEntry.arguments?.getInt(NavItem.User.key),
                verificationStep = navBackStackEntry.arguments?.getString(NavItem.VerificationStep.key)
            )
        }
    }