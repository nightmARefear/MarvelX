package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.data.models.character.toDomain
import com.alexgreece45.marvelx.domain.repositories.CharacterRepository
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

interface CharacterInteractor {
    suspend fun getAllCharacters(): Flow<CharacterPartialState>
}

class CharacterInteractorImpl @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val repository: CharacterRepository
) : CharacterInteractor {
    override suspend fun getAllCharacters(): Flow<CharacterPartialState> = flow {
        val response = repository.getAllCharacters()
        when (response.isSuccessful) {
            true -> {
                response.body()?.data?.let { it ->
                    if (it.results.isNotEmpty()) {
                        emit(
                            CharacterPartialState.Success(
                                it.results.toDomain()
                            )
                        )
                    } else {
                        emit(
                            CharacterPartialState.Failed(
                                resourceProvider.getString(R.string.no_characters_were_found_error_message)
                            )
                        )
                    }
                } ?: emit(
                    CharacterPartialState.Failed(
                        resourceProvider.getString(R.string.no_characters_were_found_error_message)
                    )
                )
            }
            false -> {
                emit(
                    CharacterPartialState.Failed(
                        response.errorBody().toString()
                    )
                )
            }
        }
    }.safeAsync {
        when (it) {
            is IOException -> {
                CharacterPartialState.NoInternetConnection(
                    resourceProvider.getString(R.string.no_internet_connection_error_message)
                )
            }
            else -> {
                CharacterPartialState.Failed(
                    it.localizedMessage ?: resourceProvider.getString(R.string.unknown_error)
                )
            }
        }
    }
}

sealed class CharacterPartialState {
    data class Success(val results: List<CharacterDomain>) : CharacterPartialState()

    data class Failed(val errorMessage: String) : CharacterPartialState()

    data class NoInternetConnection(val errorMessage: String) : CharacterPartialState()
}