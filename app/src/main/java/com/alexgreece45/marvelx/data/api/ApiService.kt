package com.alexgreece45.marvelx.data.api

import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("/v1/public/characters")
    suspend fun getCharacters() : Response<CharacterDataWrapper>
}