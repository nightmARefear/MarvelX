package com.alexgreece45.marvelx.common_ui.extensions

import androidx.compose.material.SnackbarHostState
import androidx.compose.material.SnackbarResult
import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarStateData

/**
 * Used to show a new snackbar. The currently shown snackbar is immediately dismissed.
 */
suspend fun SnackbarHostState.showSnackbarAndHandleResult(snackbarStateData: SnackbarStateData) {
    this.currentSnackbarData?.dismiss()

    try {
        val snackbarResult = this.showSnackbar(
            message = snackbarStateData.message,
            actionLabel = snackbarStateData.snackbarActionData?.label,
            duration = snackbarStateData.duration
        )

        if (snackbarResult == SnackbarResult.ActionPerformed) {
            snackbarStateData.snackbarActionData?.onClick?.invoke()
        }
    } finally {
        snackbarStateData.onDismiss?.invoke()
    }
}

fun SnackbarHostState.hideSnackbar() = this.currentSnackbarData?.dismiss()