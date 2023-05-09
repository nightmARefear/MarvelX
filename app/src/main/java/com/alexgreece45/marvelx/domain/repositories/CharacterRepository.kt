package com.alexgreece45.marvelx.domain.repositories

import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.api.ApiService
import retrofit2.Response
import javax.inject.Inject

interface CharacterRepository {
    suspend fun getAllCharacters(): Response<CharacterDataWrapper>
}

class CharacterRepositoryImpl @Inject constructor(
    private val api: ApiService
) : CharacterRepository {
    override suspend fun getAllCharacters(): Response<CharacterDataWrapper> {
        return api.getCharacters()
    }
}