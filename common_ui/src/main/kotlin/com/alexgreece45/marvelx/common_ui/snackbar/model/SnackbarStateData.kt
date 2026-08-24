package com.alexgreece45.marvelx.common_ui.snackbar.model

import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.Immutable
import java.util.UUID

/**
 * Data class representing the state of a Snackbar.
 *
 * @param uuid A unique identifier for the Snackbar message, ensuring that it's shown even if the
 * message content is the same as a previous one.
 * @param message The message to be displayed in the Snackbar.
 * @param type The type of this snackbar.
 * @param duration The duration of the Snackbar (default is [SnackbarDuration.Short]).
 * @param actionData The action data for this snackbar.
 * @param enableDragToDismiss Allow drag to dismiss functionality for this snackbar.
 * @param onDismiss The actions to perform when this snackbar is dismissed.
 */
@Immutable
data class SnackbarStateData(
    val uuid: UUID,
    val message: String,
    val type: SnackbarType,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val actionData: SnackbarActionData? = null,
    val enableDragToDismiss: Boolean = true,
    val onDismiss: ((uuid: UUID) -> Unit)? = null
)