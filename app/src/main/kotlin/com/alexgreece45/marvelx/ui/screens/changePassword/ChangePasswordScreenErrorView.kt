package com.alexgreece45.marvelx.ui.screens.changePassword

import com.alexgreece45.marvelx.ui.components.models.ErrorButtonLayout

data class ChangePasswordScreenErrorView(
    val errorTitle: String,
    val errorSubtitle: String,
    val onCloseClicked: (() -> Unit)? = null,
    val errorButtonLayout: ErrorButtonLayout.Single
)