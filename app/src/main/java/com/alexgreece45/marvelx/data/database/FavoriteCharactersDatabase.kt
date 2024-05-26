package com.alexgreece45.marvelx.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

//@Database(entities = [CharacterEntity::class], version = 1, exportSchema = BuildConfig.EXPORT_DATABASE_SCHEMA.toBoolean())
@Database(entities = [FavoriteCharacterEntity::class], version = 1, exportSchema = false)
abstract class FavoriteCharactersDatabase : RoomDatabase() {
    abstract val favoriteCharactersDao: FavoriteCharactersDao
}