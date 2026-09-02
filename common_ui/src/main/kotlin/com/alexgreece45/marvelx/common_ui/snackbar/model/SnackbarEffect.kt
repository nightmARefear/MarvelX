package com.alexgreece45.marvelx.common_ui.snackbar.model

import java.util.UUID

sealed interface SnackbarEffect {
    data class Show(val snackbarStateData: SnackbarStateData) : SnackbarEffect
    data class OnDismissed(val uuid: UUID) : SnackbarEffect
}