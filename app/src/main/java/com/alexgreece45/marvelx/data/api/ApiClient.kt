package com.alexgreece45.marvelx.data.api

import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.di.ApiResult
import retrofit2.Response

interface ApiClient {
    suspend fun getCharacters(): Response<CharacterDataWrapper>

    suspend fun getCharacter(characterId: Int): Response<CharacterDataWrapper>

    suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper>
}