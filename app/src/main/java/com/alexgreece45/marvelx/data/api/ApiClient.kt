package com.alexgreece45.marvelx.data.api

import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.data.models.user.UserSignUpRequest
import com.alexgreece45.marvelx.data.models.user.UserSignUpResponse
import com.alexgreece45.marvelx.di.ApiResult
import retrofit2.Response

interface ApiClient {
    suspend fun getCharacters(): Response<CharacterDataWrapper>

    suspend fun getCharacterById(characterId: Int): Response<CharacterDataWrapper>

    suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper>

    suspend fun getComicById(comicId: Int): Response<ComicDataWrapper>

    suspend fun postSignUp(userSignUpRequest: UserSignUpRequest): Response<UserSignUpResponse>
}