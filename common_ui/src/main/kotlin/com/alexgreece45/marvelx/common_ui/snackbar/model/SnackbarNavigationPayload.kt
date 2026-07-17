package com.alexgreece45.marvelx.common_ui.snackbar.model

import android.os.Parcelable
import androidx.compose.material.SnackbarDuration
import kotlinx.parcelize.Parcelize

/**
 * Snackbar payload for navigation purposes. If [actionLabel] is not null, then the action is to
 * close the snackbar.
 */
@Parcelize
data class SnackbarNavigationPayload(
    val message: String,
    val type: SnackbarType,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val actionLabel: String? = null
) : Parcelable