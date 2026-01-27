package com.alexgreece45.marvelx.database

import androidx.room.Database
import androidx.room.RoomDatabase

//@Database(entities = [CharacterEntity::class], version = 1, exportSchema = BuildConfig.EXPORT_DATABASE_SCHEMA.toBoolean())
@Database(
    entities = [
        FavoriteCharacterEntity::class,
        ListingCategoryEntity::class,
        ListingSubcategoryEntity::class
    ],
    version = 1,
    exportSchema = false // False for security reasons.
)
abstract class MarvelXDatabase : RoomDatabase() {

    abstract val favoriteCharactersDao: FavoriteCharactersDao
    abstract val listingCategoryDao: ListingCategoryDao
}
