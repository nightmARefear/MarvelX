package com.alexgreece45.marvelx.ui.screens.home.models

import com.alexgreece45.marvelx.ui.screens.home.HomeScreen

/**
 * Defines an alert dialog error in [HomeScreen].
 */
sealed interface HomeAlertDialogErrorType {
    data object SessionExpired : HomeAlertDialogErrorType
    data class HasError(val errorMessage: String?) : HomeAlertDialogErrorType
}