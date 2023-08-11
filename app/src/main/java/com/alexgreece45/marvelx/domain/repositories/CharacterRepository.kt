package com.alexgreece45.marvelx.domain.repositories

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.character.CharacterDataWrapper
import com.alexgreece45.marvelx.data.models.character.comic.ComicResponse
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import logcat.LogPriority
import logcat.logcat
import okio.IOException
import retrofit2.Response
import javax.inject.Inject

interface CharacterRepository {
    suspend fun getAllCharacters(): Response<CharacterDataWrapper>

    suspend fun getCharacter(characterId: Int): Response<CharacterDataWrapper>

    suspend fun getCharacterComics(characterId: Int): Flow<CharacterRepositoryPartialState>
}

class CharacterRepositoryImpl @Inject constructor(
    private val api: ApiClient,
    private val resourceProvider: ResourceProvider
) : CharacterRepository {
    override suspend fun getAllCharacters(): Response<CharacterDataWrapper> {
        return api.getCharacters()
    }

    override suspend fun getCharacter(characterId: Int): Response<CharacterDataWrapper> {
        return api.getCharacterById(characterId)
    }

    override suspend fun getCharacterComics(characterId: Int): Flow<CharacterRepositoryPartialState> =
        flow {
            try {
                val response = api.getCharacterComics(characterId)

                if (response.isSuccessful) {
                    response.body()?.let {
                        emit(
                            CharacterRepositoryPartialState.Success(
                                data = it.data.results
                            )
                        )
                    } ?: run {
                        CharacterRepositoryPartialState.Error(
                            errorCode = response.code(),
                            errorMessage = resourceProvider.getString(R.string.unknown_error)
                        )
                    }
                } else {
                    emit(
                        CharacterRepositoryPartialState.Error(
                            errorCode = response.code(),
                            errorMessage = resourceProvider.getString(R.string.no_data_was_found)
                        )
                    )
                }
            } catch (e: IOException) {
                logcat(LogPriority.ERROR) { e.toString() }
                emit(
                    CharacterRepositoryPartialState.NoInternetConnection(
                        errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                    )
                )
            } catch (e: Exception) {
                logcat(LogPriority.ERROR) { e.toString() }
                emit(
                    CharacterRepositoryPartialState.Error(
                        errorMessage = e.localizedMessage
                            ?: resourceProvider.getString(R.string.unknown_error)
                    )
                )
            }
        }
}

sealed interface CharacterRepositoryPartialState {
    data class Success(val data: List<ComicResponse>) : CharacterRepositoryPartialState

    data class Error(
        val errorCode: Int? = null,
        val errorMessage: String
    ) : CharacterRepositoryPartialState

    data class NoInternetConnection(val errorMessage: String) : CharacterRepositoryPartialState
}