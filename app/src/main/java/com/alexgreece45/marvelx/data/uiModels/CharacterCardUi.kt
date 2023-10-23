package com.alexgreece45.marvelx.data.uiModels

import com.alexgreece45.marvelx.data.domains.ImageDomain

data class CharacterCardUi(
    val id: Int,
    val name: String,
    val imageDownloadUrl: String,
    val imageContentDescription: String,
    val isFavorite: Boolean = false
)
