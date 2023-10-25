package com.alexgreece45.marvelx.domain.repositories

import com.alexgreece45.marvelx.domain.database.FavoriteCharacterEntity
import com.alexgreece45.marvelx.domain.database.FavoriteCharactersDao
import javax.inject.Inject

interface FavoriteCharactersRepository {
    suspend fun insertCharacters(vararg favoriteCharacterEntity: FavoriteCharacterEntity): LongArray

    suspend fun deleteCharacters(vararg favoriteCharacterEntity: FavoriteCharacterEntity): Int

    suspend fun deleteCharacterById(characterId: Int): Int

    suspend fun getAllCharacters(): List<FavoriteCharacterEntity>

    suspend fun getCharacterIds(): List<Int>

    suspend fun getCharacterById(characterId: Int): FavoriteCharacterEntity
}

class FavoriteCharactersRepositoryImpl @Inject constructor(
    private val favoriteCharactersDao: FavoriteCharactersDao
) : FavoriteCharactersRepository {
    override suspend fun insertCharacters(vararg favoriteCharacterEntity: FavoriteCharacterEntity): LongArray =
        favoriteCharactersDao.insertCharacters(*favoriteCharacterEntity)

    override suspend fun deleteCharacters(vararg favoriteCharacterEntity: FavoriteCharacterEntity): Int =
        favoriteCharactersDao.deleteCharacters(*favoriteCharacterEntity)

    override suspend fun deleteCharacterById(characterId: Int): Int =
        favoriteCharactersDao.deleteCharacterById(characterId = characterId)

    override suspend fun getAllCharacters(): List<FavoriteCharacterEntity> =
        favoriteCharactersDao.getAllCharacters()

    override suspend fun getCharacterIds(): List<Int> =
        favoriteCharactersDao.getCharacterIds()

    override suspend fun getCharacterById(characterId: Int): FavoriteCharacterEntity =
        favoriteCharactersDao.getCharacterById(characterId = characterId)
}