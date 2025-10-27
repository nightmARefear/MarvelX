package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model

data class ProfileDetailsStatsErrorView(
    val subtitle: String,
    val primaryButtonLabel: String,
    val primaryButtonOnClicked: () -> Unit
)
