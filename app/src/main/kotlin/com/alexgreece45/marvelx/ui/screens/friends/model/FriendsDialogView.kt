package com.alexgreece45.marvelx.ui.screens.friends.model

data class FriendsDialogView(
    val title: String,
    val subtitle: String,
    val negativeButtonLabel: String,
    val negativeButtonOnClicked: () -> Unit,
    val positiveButtonLabel: String,
    val positiveButtonOnClicked: () -> Unit,
)
