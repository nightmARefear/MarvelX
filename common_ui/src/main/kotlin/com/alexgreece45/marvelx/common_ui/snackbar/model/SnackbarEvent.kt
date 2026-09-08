package com.alexgreece45.marvelx.common_ui.snackbar.model

import java.util.UUID

sealed interface SnackbarEvent {
    data class Show(val snackbarStateData: SnackbarStateData) : SnackbarEvent
    data class OnDismissed(val uuid: UUID) : SnackbarEvent
}