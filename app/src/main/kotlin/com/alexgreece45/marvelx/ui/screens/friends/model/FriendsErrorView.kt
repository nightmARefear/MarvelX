package com.alexgreece45.marvelx.ui.screens.friends.model

data class FriendsErrorView(
    val tabIndex: Int,
    val subtitle: String,
    val primaryButtonOnClicked: () -> Unit
)
