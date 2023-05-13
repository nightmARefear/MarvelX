package com.alexgreece45.marvelx.domain.repositories

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.api.ApiService
import retrofit2.Response
import javax.inject.Inject

interface CharacterRepository {
    suspend fun getAllCharacters(): Response<CharacterDataWrapper>
    suspend fun getCharacter(characterId: Int) : Response<CharacterDataWrapper>
}

class CharacterRepositoryImpl @Inject constructor(
    private val api: ApiClient
) : CharacterRepository {
    override suspend fun getAllCharacters(): Response<CharacterDataWrapper> {
        return api.getCharacters()
    }

    override suspend fun getCharacter(characterId: Int): Response<CharacterDataWrapper> {
        return api.getCharacter(characterId)
    }
}