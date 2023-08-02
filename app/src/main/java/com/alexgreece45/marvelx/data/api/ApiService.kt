package com.alexgreece45.marvelx.data.api

import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataWrapper
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("/v1/public/characters")
    suspend fun getCharacters(): Response<CharacterDataWrapper>

    @GET("/v1/public/characters/{characterId}")
    suspend fun getCharacter(@Path(value = "characterId") characterId: Int): Response<CharacterDataWrapper>

    @GET("/v1/public/characters/{characterId}/comics")
    suspend fun getCharacterComics(@Path(value = "characterId") characterId: Int): Response<ComicDataWrapper>

    @GET("/v1/public/comics/{comicId}")
    suspend fun getComicById(@Path(value = "comicId") comicId: Int) : Response<ComicDataWrapper>
}