package com.alexgreece45.marvelx.ui.screens.profile.image.model

import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout

data class ProfileImageErrorView(
    val title:String,
    val subtitle:String,
    val errorButtonLayout: ErrorButtonLayout.Single
)
