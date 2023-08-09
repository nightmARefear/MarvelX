package com.alexgreece45.marvelx.domain.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexgreece45.marvelx.BuildConfig

@Entity(tableName = "favorite_character")
data class FavoriteCharacterEntity(
    @PrimaryKey
    @ColumnInfo(name = "characterId")
    val characterId: Int,
    @ColumnInfo(name = "favorite", defaultValue = "false")
    val favorite: Boolean
)