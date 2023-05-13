package com.alexgreece45.marvelx.data.api

import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import retrofit2.Response

interface ApiClient {
    suspend fun getCharacters() : Response<CharacterDataWrapper>

    suspend fun getCharacter(characterId: Int) : Response<CharacterDataWrapper>
}