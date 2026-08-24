package com.alexgreece45.marvelx.common_ui.snackbar.model

sealed interface SnackbarActionType {
    data object Hide : SnackbarActionType
    data class Custom(val onAction: () -> Unit) : SnackbarActionType
}