package com.alexgreece45.marvelx.ui.navigation.models

/**
 * Defines a single navigation argument used for navigation purposes.
 */
sealed class NavArg(val key: String) {
    data object HomeLogout : NavArg(key = "homeLogout")
    data object HomeAlertDialogErrorType : NavArg(key = "homeAlertDialogErrorType")
    data object SplashAlertDialogErrorType : NavArg(key = "splashAlertDialogErrorType")
    data object ProfileImageUri : NavArg(key = "profileImageUri")
}
