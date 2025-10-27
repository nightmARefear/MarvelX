package com.alexgreece45.marvelx.ui.screens.allCharacters.uiModel

import com.alexgreece45.marvelx.database.FavoriteCharacterEntity

data class CharacterCardUi(
    val id: Int,
    val name: String,
    val imageDownloadUrl: String,
    val imageContentDescription: String,
    val isFavorite: Boolean = false
)

fun CharacterCardUi.toFavoriteCharacterEntity(): FavoriteCharacterEntity =
    FavoriteCharacterEntity(
        characterId = this.id,
        name = this.name,
        imageDownloadUrl = this.imageDownloadUrl,
        imageContentDescription = this.imageContentDescription,
        isFavorite = this.isFavorite
    )
