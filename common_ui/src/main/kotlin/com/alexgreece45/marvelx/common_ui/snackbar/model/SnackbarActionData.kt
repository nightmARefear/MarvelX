package com.alexgreece45.marvelx.common_ui.snackbar.model

/**
 * @param label The label of the action button of the snackbar.
 * @param onClick The action to perform when the action button of the snackbar is clicked.
 */
data class SnackbarActionData(
    val label: String,
    val onClick: () -> Unit
)