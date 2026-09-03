package com.alexgreece45.marvelx.common_ui.messageBanner.model

import com.alexgreece45.marvelx.common_ui.snackbar.model.SnackbarActionType

data class MessageBannerActionData(
    val label: String,
    val actionType: MessageBannerActionType
)