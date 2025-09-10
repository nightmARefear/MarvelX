package com.alexgreece45.marvelx.ui.components.model

import androidx.compose.material.SnackbarDuration
import java.util.UUID

/**
 * Data class representing the state of a Snackbar.
 *
 * @param message       The message to be displayed in the Snackbar.
 * @param actionLabel   The label for the optional action button in the Snackbar.
 * @param duration      The duration of the Snackbar (default is [SnackbarDuration.Short]).
 * @param uuid          A unique identifier for the Snackbar message, ensuring that it's shown even
 * if the message content is the same as a previous one.
 */
data class SnackbarStateData(
    val message: String,
    val actionLabel: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val uuid: UUID = UUID.randomUUID()
)
