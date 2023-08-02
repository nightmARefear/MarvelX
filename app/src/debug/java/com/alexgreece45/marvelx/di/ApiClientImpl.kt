package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.api.ApiService
import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicDataWrapper
import com.alexgreece45.marvelx.util.ResourceProvider
import com.google.gson.Gson
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Response
import javax.inject.Inject

@Module
@InstallIn(SingletonComponent::class)
class ApiClientImpl @Inject constructor(
    private val api: ApiService,
    private val gson: Gson,
    private val resourceProvider: ResourceProvider
) : ApiClient {

    override suspend fun getCharacters(): Response<CharacterDataWrapper> {
        return api.getCharacters()
    }

    override suspend fun getCharacter(characterId: Int): Response<CharacterDataWrapper> {
        return api.getCharacter(characterId)
    }

    override suspend fun getCharacterComics(characterId: Int): Response<ComicDataWrapper> {
        return api.getCharacterComics(characterId)
    }

    override suspend fun getComicById(comicId: Int): Response<ComicDataWrapper> {
        return api.getComicById(comicId)
    }
}

sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(
        val errorCode: Int? = null,
        val errorMessage: String
    ) : ApiResult<Nothing>

    data class NoInternetConnection(
        val errorMessage: String
    ) : ApiResult<Nothing>
}