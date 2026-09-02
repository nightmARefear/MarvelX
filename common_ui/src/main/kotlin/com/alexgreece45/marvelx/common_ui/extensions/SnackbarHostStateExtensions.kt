package com.alexgreece45.marvelx.common_ui.extensions

import androidx.compose.material.SnackbarHostState
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarStateData

/**
 * Used to show a new snackbar. The currently shown snackbar is immediately dismissed.
 */
suspend fun SnackbarHostState.showSnackbarAndHandleResult(snackbarStateData: SnackbarStateData) {
    this.currentSnackbarData?.dismiss()

    try {
        this.showSnackbar(
            message = snackbarStateData.message,
            actionLabel = snackbarStateData.actionData?.label,
            duration = snackbarStateData.duration
        )
    } finally {
        snackbarStateData.onDismiss?.invoke(snackbarStateData.uuid)
    }
}

fun SnackbarHostState.hideSnackbar() = this.currentSnackbarData?.dismiss()