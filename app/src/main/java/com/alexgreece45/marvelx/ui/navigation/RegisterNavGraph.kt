package com.alexgreece45.marvelx.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.navigation
import com.alexgreece45.marvelx.ui.screens.changePassword.ChangePasswordScreen
import com.alexgreece45.marvelx.ui.screens.otpScreen.OtpScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.homeScreen.HomeScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.loginScreen.LoginScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.loginScreen.forgotPasswordScreen.ForgotPasswordScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.loginScreen.forgotPasswordScreen.forgotPasswordValidateEmailScreen.ForgotPasswordValidateEmailScreen
import com.alexgreece45.marvelx.ui.screens.registerScreens.signupScreen.SignUpScreen
import com.alexgreece45.marvelx.ui.screens.splashScreen.SplashScreen

@Suppress("FunctionName")
fun NavGraphBuilder.RegisterNavGraph(navController: NavController) {
    navigation(
        route = Screen.RegisterScreens.getNestedGraphRootRoute(),
        startDestination = Screen.RegisterScreens.getStartDestination()
    ) {
        composable(Screen.RegisterScreens.SplashScreen) {
            SplashScreen(navController = navController)
        }

        composable(Screen.RegisterScreens.HomeScreen) {
            HomeScreen(
                navController = navController
            )
        }

        composable(Screen.RegisterScreens.LoginScreens.LoginScreen) {
            LoginScreen(
                navController = navController
            )
        }

        composable(Screen.RegisterScreens.LoginScreens.ForgotPasswordScreens.ForgotPasswordScreen) {
            ForgotPasswordScreen(
                popUpToLoginScreen = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(Screen.RegisterScreens.LoginScreens.ForgotPasswordScreens.ForgotPasswordValidateEmailScreen) { entry ->
            ForgotPasswordValidateEmailScreen(
                popBackStack = { navController.popBackStack() },
                navigateToForgotPasswordEnterPhoneNumberScreen = { requestIdEmail ->
                    navController.navigate(
                        Screen.RegisterScreens.LoginScreens.ForgotPasswordScreens.ForgotPasswordEnterPhoneNumberScreen.createNavRoute(
                            requestIdEmail = requestIdEmail
                        )
                    )
                },
                requestIdEmail = entry.arguments?.getString(NavItem.RequestIdEmail.key)
            )
        }

        composable(Screen.RegisterScreens.ChangePasswordScreen) { entry ->
            ChangePasswordScreen(
                popBackStack = { navController.popBackStack() },
                otpFlowType = entry.arguments?.getString(NavItem.OtpFlowType.key),
                requestIdEmail = entry.arguments?.getString(NavItem.RequestIdEmail.key).orEmpty(),
                requestIdPhoneNumber = entry.arguments?.getString(NavItem.RequestIdPhoneNumber.key)
            )
        }

        composable(Screen.RegisterScreens.SignupScreen) {
            SignUpScreen(navController = navController)
        }

        composable(Screen.OtpScreen) { navBackStackEntry ->
            OtpScreen(
                navController = navController,
                userId = navBackStackEntry.arguments?.getInt(NavItem.UserId.key),
                otpFlowType = navBackStackEntry.arguments?.getString(NavItem.OtpFlowType.key),
                verificationStep = navBackStackEntry.arguments?.getString(NavItem.VerificationStep.key),
                shouldRememberUserSession = navBackStackEntry.arguments?.getBoolean(NavItem.ShouldRememberUserSession.key),
                requestIdEmail = navBackStackEntry.arguments?.getString(NavItem.RequestIdEmail.key),
                requestIdPhoneNumber = navBackStackEntry.arguments?.getString(NavItem.RequestIdPhoneNumber.key)
            )
        }
    }
}