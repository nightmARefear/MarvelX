package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.data.domains.ComicDomain
import com.alexgreece45.marvelx.data.models.character.comic.ComicResponse
import com.alexgreece45.marvelx.data.models.character.comic.toDomain
import com.alexgreece45.marvelx.data.models.character.toDomain
import com.alexgreece45.marvelx.domain.database.FavoriteCharacterEntity
import com.alexgreece45.marvelx.domain.repositories.CharacterRepository
import com.alexgreece45.marvelx.domain.repositories.CharacterRepositoryPartialState
import com.alexgreece45.marvelx.domain.repositories.FavoriteCharactersRepository
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import logcat.logcat
import okio.IOException
import javax.inject.Inject

interface CharacterInteractor {
    suspend fun getAllCharacters(): Flow<CharacterPartialState<List<CharacterDomain>>>
    suspend fun getCharacter(characterId: Int): Flow<CharacterPartialState<CharacterDomain>>

    suspend fun getCharacterComics(characterId: Int): Flow<GetCharacterComicsPartialState>

    suspend fun insertFavoriteCharacters(vararg favoriteCharacterEntity: FavoriteCharacterEntity): Flow<CharacterPartialState<Int>>

    suspend fun deleteFavoriteCharacterById(characterId: Int): Flow<CharacterPartialState<Int>>

    suspend fun getFavoriteCharacters(): Flow<GetFavoriteCharactersPartialState>

    suspend fun isCharacterByIdFavorite(characterId: Int): Flow<IsCharacterFavoritePartialState>
}

class CharacterInteractorImpl @Inject constructor(
    private val resourceProvider: ResourceProvider,
    private val characterRepository: CharacterRepository,
    private val favoriteCharactersRepository: FavoriteCharactersRepository
) : CharacterInteractor {
    override suspend fun getAllCharacters(): Flow<CharacterPartialState<List<CharacterDomain>>> =
        flow {
            val response = characterRepository.getAllCharacters()
            when (response.isSuccessful) {
                true -> {
                    response.body()?.data?.let {
                        if (it.results.isNotEmpty()) {
                            emit(
                                CharacterPartialState.Success(
                                    it.results.toDomain()
                                )
                            )
                        } else {
                            emit(
                                CharacterPartialState.Error(
                                    errorMessage = resourceProvider.getString(R.string.no_characters_were_found_error_message)
                                )
                            )
                        }
                    } ?: run {
                        emit(
                            CharacterPartialState.Error(
                                errorMessage = resourceProvider.getString(R.string.no_characters_were_found_error_message)
                            )
                        )
                    }
                }

                false -> {
                    emit(
                        CharacterPartialState.Error(
                            errorMessage = response.errorBody().toString()
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
                    CharacterPartialState.Error(
                        errorMessage = it.localizedMessage
                            ?: resourceProvider.getString(R.string.unknown_error)
                    )
                }
            }
        }

    override suspend fun getCharacter(characterId: Int): Flow<CharacterPartialState<CharacterDomain>> =
        flow {
            val response = characterRepository.getCharacter(characterId)
            when (response.isSuccessful) {
                true -> {
                    response.body()?.data?.let {
                        if (it.results.isNotEmpty()) {
                            emit(
                                CharacterPartialState.Success(
                                    it.results[0].toDomain()
                                )
                            )
                        } else {
                            emit(
                                CharacterPartialState.Error(
                                    errorMessage = resourceProvider.getString(
                                        R.string.character_with_specific_id_not_found,
                                        characterId
                                    )
                                )
                            )
                        }
                    } ?: run {
                        emit(
                            CharacterPartialState.Error(
                                errorMessage = resourceProvider.getString(R.string.character_error_loading)
                            )
                        )
                    }
                }

                false -> {
                    emit(
                        CharacterPartialState.Error(
                            errorMessage = response.errorBody().toString()
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
                    CharacterPartialState.Error(
                        errorMessage = it.localizedMessage
                            ?: resourceProvider.getString(R.string.unknown_error)
                    )
                }
            }
        }

    override suspend fun getCharacterComics(characterId: Int): Flow<GetCharacterComicsPartialState> =
        flow {
            characterRepository.getCharacterComics(characterId).collect {
                when (it) {
                    is CharacterRepositoryPartialState.Error -> {
                        emit(
                            GetCharacterComicsPartialState.Error(
                                errorCode = it.errorCode,
                                errorMessage = it.errorMessage
                            )
                        )
                    }

                    is CharacterRepositoryPartialState.NoInternetConnection -> {
                        emit(
                            GetCharacterComicsPartialState.NoInternetConnection
                        )
                    }

                    is CharacterRepositoryPartialState.Success -> {
                        emit(
                            GetCharacterComicsPartialState.Success(
                                comics = it.data
                            )
                        )
                    }
                }
            }
        }.safeAsync {
            when (it) {
                is IOException -> {
                    GetCharacterComicsPartialState.NoInternetConnection
                }

                else -> {
                    GetCharacterComicsPartialState.Error(
                        errorMessage = it.localizedMessage
                            ?: resourceProvider.getString(R.string.unknown_error)
                    )
                }
            }
        }

    override suspend fun insertFavoriteCharacters(vararg favoriteCharacterEntity: FavoriteCharacterEntity): Flow<CharacterPartialState<Int>> =
        flow {
            val insertedCharactersSize =
                favoriteCharactersRepository.insertCharacters(favoriteCharacterEntity = favoriteCharacterEntity).size
            when (insertedCharactersSize == favoriteCharacterEntity.size) {
                true -> {
                    emit(
                        CharacterPartialState.Success(data = favoriteCharacterEntity.size)
                    )
                }

                false -> {
                    emit(
                        CharacterPartialState.Error(
                            errorMessage = resourceProvider.getString(R.string.all_characters_screen_error_favoring_character)
                        )
                    )
                }
            }
        }.safeAsync {
            logcat { it.localizedMessage.orEmpty() }
            CharacterPartialState.Error(
                errorMessage = resourceProvider.getString(R.string.all_characters_screen_error_favoring_character)
            )
        }

    override suspend fun deleteFavoriteCharacterById(characterId: Int): Flow<CharacterPartialState<Int>> =
        flow {
            when (favoriteCharactersRepository.deleteCharacterById(characterId = characterId)) {
                1 -> {
                    emit(CharacterPartialState.Success(1))
                }

                else -> {
                    emit(
                        CharacterPartialState.Error(
                            errorMessage = resourceProvider.getString(
                                R.string.all_characters_screen_error_unfavoring_character
                            )
                        )
                    )
                }
            }
        }.safeAsync {
            logcat { it.localizedMessage ?: "" }
            CharacterPartialState.Error(
                errorMessage = resourceProvider.getString(R.string.all_characters_screen_error_favoring_character)
            )
        }

    override suspend fun getFavoriteCharacters(): Flow<GetFavoriteCharactersPartialState> =
        flow {
            emit(
                GetFavoriteCharactersPartialState.Success(
                    favoriteCharactersRepository.getAllCharacters()
                )
            )
        }.safeAsync {
            logcat { it.localizedMessage ?: "" }
            GetFavoriteCharactersPartialState.Error(
                errorMessage = resourceProvider.getString(R.string.all_characters_screen_error_favoring_character)
            )
        }

    override suspend fun isCharacterByIdFavorite(characterId: Int): Flow<IsCharacterFavoritePartialState> =
        flow {
            emit(
                IsCharacterFavoritePartialState.IsFavorite(
                    favoriteCharacter = favoriteCharactersRepository.getCharacterById(characterId)
                )
            )
        }.safeAsync {
            IsCharacterFavoritePartialState.IsNotFavorite
        }
}

sealed class CharacterPartialState<out T> {
    data class Success<T>(val data: T) : CharacterPartialState<T>()

    data class Error(
        val errorCode: Int? = null,
        val errorMessage: String
    ) : CharacterPartialState<Nothing>()

    data class NoInternetConnection(val errorMessage: String) : CharacterPartialState<Nothing>()
}

sealed class GetCharacterComicsPartialState {
    data class Success(val comics: List<ComicResponse>) : GetCharacterComicsPartialState()

    data class Error(
        val errorCode: Int? = null,
        val errorMessage: String
    ) : GetCharacterComicsPartialState()

    object NoInternetConnection : GetCharacterComicsPartialState()
}

sealed class DeleteFavoriteCharacterPartialState {
    data class Success(val favoriteCharacter: FavoriteCharacterEntity) :
        DeleteFavoriteCharacterPartialState()

    data class Error(val errorMessage: String) : DeleteFavoriteCharacterPartialState()
}

sealed class GetFavoriteCharactersPartialState {
    data class Success(val favoriteCharacters: List<FavoriteCharacterEntity>) :
        GetFavoriteCharactersPartialState()

    data class Error(val errorMessage: String) : GetFavoriteCharactersPartialState()
}

sealed class IsCharacterFavoritePartialState {
    data class IsFavorite(val favoriteCharacter: FavoriteCharacterEntity) :
        IsCharacterFavoritePartialState()

    object IsNotFavorite : IsCharacterFavoritePartialState()
}
