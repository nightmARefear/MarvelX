package com.alexgreece45.marvelx.ui.screens.friends.model

data class FriendsErrorView(
    val subtitle: String,
    val primaryButtonLabel: String,
    val primaryButtonOnClicked: () -> Unit
)
