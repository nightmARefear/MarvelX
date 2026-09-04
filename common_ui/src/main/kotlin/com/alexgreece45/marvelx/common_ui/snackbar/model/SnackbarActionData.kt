package com.alexgreece45.marvelx.common_ui.snackbar.model

/**
 * @param label The label of the action button of the snackbar.
 * @param actionType The type of snackbar action type. If [SnackbarActionType.Hide] then the snackbar
 * hides when the action button is clicked, otherwise a custom action is performed.
 */
data class SnackbarActionData(
    val label: String,
    val actionType: SnackbarActionType
)