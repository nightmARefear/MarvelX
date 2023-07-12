package com.alexgreece45.marvelx.domain.repositories

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.domains.ComicDomain
import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.toDomain
import com.alexgreece45.marvelx.domain.interactors.CharacterPartialState
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import logcat.logcat
import okio.IOException
import retrofit2.Response
import javax.inject.Inject

interface CharacterRepository {
    suspend fun getAllCharacters(): Response<CharacterDataWrapper>

    suspend fun getCharacter(characterId: Int): Response<CharacterDataWrapper>

    suspend fun getCharacterComics(characterId: Int): Flow<CharacterPartialState<List<ComicDomain>>>
}

class CharacterRepositoryImpl @Inject constructor(
    private val api: ApiClient,
    private val resourceProvider: ResourceProvider
) : CharacterRepository {
    override suspend fun getAllCharacters(): Response<CharacterDataWrapper> {
        return api.getCharacters()
    }

    override suspend fun getCharacter(characterId: Int): Response<CharacterDataWrapper> {
        return api.getCharacter(characterId)
    }

    override suspend fun getCharacterComics(characterId: Int): Flow<CharacterPartialState<List<ComicDomain>>> =
        flow {
            try {
                val response = api.getCharacterComics(characterId)

                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(CharacterPartialState.Success(data = it.data.results.toDomain()))
                    } ?: {
                        CharacterPartialState.Error(
                            errorCode = response.code(),
                            errorMessage = resourceProvider.getString(R.string.unknown_error)
                        )
                    }
                } else {
                    emit(
                        CharacterPartialState.Error(
                            errorCode = response.code(),
                            errorMessage = resourceProvider.getString(R.string.no_data_was_found)
                        )
                    )
                }
            } catch (e: IOException) {
                logcat { e.toString() }
                emit(
                    CharacterPartialState.NoInternetConnection(
                        errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                    )
                )
            } catch (e: Exception) {
                logcat { e.toString() }
                emit(
                    CharacterPartialState.Error(
                        errorMessage = e.localizedMessage
                            ?: resourceProvider.getString(R.string.unknown_error)
                    )
                )
            }
        }
}