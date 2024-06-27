package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.uiModel

import androidx.annotation.StringRes

data class ProfileDetailsStatsGenericUi(
    val title: String,
    val imageUrl: String,
    @StringRes val contentDescription: Int
)
