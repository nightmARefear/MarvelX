package com.alexgreece45.marvelx.domain.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexgreece45.marvelx.data.uiModels.CharacterCardUi
import com.alexgreece45.marvelx.data.uiModels.toFavoriteCharacterEntity

@Entity(tableName = "favorite_character")
data class FavoriteCharacterEntity(
    @PrimaryKey
    @ColumnInfo(name = "characterId", index = true)
    val characterId: Int,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "imageDownloadUrl")
    val imageDownloadUrl: String,

    @ColumnInfo(name = "imageContentDescription")
    val imageContentDescription: String,

    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean
)

fun FavoriteCharacterEntity.toCharacterCardUi(): CharacterCardUi =
    CharacterCardUi(
        id = this.characterId,
        name = this.name,
        imageDownloadUrl = this.imageDownloadUrl,
        imageContentDescription = this.imageContentDescription,
        isFavorite = this.isFavorite
    )

fun List<FavoriteCharacterEntity>.toCharacterCardUiList(): List<CharacterCardUi> =
    map { favoriteCharacterEntity -> favoriteCharacterEntity.toCharacterCardUi() }