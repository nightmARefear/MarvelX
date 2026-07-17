package com.alexgreece45.marvelx.common_ui.snackbar.model

import androidx.compose.material.SnackbarDuration
import com.alexgreece45.marvelx.core.common.util.safeLet
import java.util.UUID

/**
 * Data class representing the state of a Snackbar.
 *
 * @param uuid A unique identifier for the Snackbar message, ensuring that it's shown even if the
 * message content is the same as a previous one.
 * @param message The message to be displayed in the Snackbar.
 * @param type The type of this snackbar.
 * @param duration The duration of the Snackbar (default is [SnackbarDuration.Short]).
 * @param snackbarActionData   The action data for this snackbar.
 * @param onDismiss The actions to perform when this snackbar is dismissed.
 */
data class SnackbarStateData(
    val uuid: UUID,
    val message: String,
    val type: SnackbarType,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val snackbarActionData: SnackbarActionData? = null,
    val onDismiss: (() -> Unit)? = null
)

fun SnackbarNavigationPayload.toStateData(
    uuid: UUID,
    onAction: (() -> Unit)? = null,
    onDismiss: () -> Unit
): SnackbarStateData =
    SnackbarStateData(
        uuid = uuid,
        message = this.message,
        type = this.type,
        duration = this.duration,
        snackbarActionData = safeLet(actionLabel, onAction) { label, onAction ->
            SnackbarActionData(
                label = label,
                onClick = onAction
            )
        },
        onDismiss = onDismiss
    )