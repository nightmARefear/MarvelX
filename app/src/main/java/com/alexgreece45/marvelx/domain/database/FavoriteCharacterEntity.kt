package com.alexgreece45.marvelx.domain.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_character")
data class FavoriteCharacterEntity(
    @PrimaryKey
    @ColumnInfo(name = "characterId")
    val characterId: Int
)