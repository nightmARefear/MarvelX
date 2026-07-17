package com.alexgreece45.marvelx.common_ui.snackbar.model

import androidx.compose.material.SnackbarHostState

data class SnackbarConfig(
    val snackbarHostState: SnackbarHostState,
    val snackbarStateData: SnackbarStateData?
)