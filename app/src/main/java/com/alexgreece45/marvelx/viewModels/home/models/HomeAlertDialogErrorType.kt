package com.alexgreece45.marvelx.viewModels.home.models

sealed interface HomeAlertDialogErrorType {
    data object SessionExpired : HomeAlertDialogErrorType
    data class HasError(val errorMessage: String) : HomeAlertDialogErrorType
}