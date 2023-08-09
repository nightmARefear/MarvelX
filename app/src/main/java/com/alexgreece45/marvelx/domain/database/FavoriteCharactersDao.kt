package com.alexgreece45.marvelx.domain.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteCharactersDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCharacters(vararg favoriteCharacterEntity: FavoriteCharacterEntity): LongArray

    @Delete
    suspend fun deleteCharacters(vararg favoriteCharacterEntity: FavoriteCharacterEntity): Int

    @Query("DELETE FROM favorite_character WHERE characterId = :characterId")
    suspend fun deleteCharacterById(characterId: Int): Int

    @Query("SELECT * FROM favorite_character")
    suspend fun getAllCharacters(): List<FavoriteCharacterEntity>

    @Query("SELECT * FROM favorite_character WHERE characterId = :characterId")
    suspend fun getCharacterById(characterId: Int): FavoriteCharacterEntity
}