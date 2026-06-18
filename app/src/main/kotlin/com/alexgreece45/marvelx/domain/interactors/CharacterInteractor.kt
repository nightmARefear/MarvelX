package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.repository.CharacterRepository
import com.alexgreece45.marvelx.data.repository.FavoriteCharactersRepository
import com.alexgreece45.marvelx.data.repository.GetAllCharactersRepositoryResponse
import com.alexgreece45.marvelx.data.repository.GetCharacterComicsRepositoryResponse
import com.alexgreece45.marvelx.data.repository.GetCharacterRepositoryResponse
import com.alexgreece45.marvelx.database.FavoriteCharacterEntity
import com.alexgreece45.marvelx.domain.model_api.character.CharacterDomain
import com.alexgreece45.marvelx.domain.model_api.character.toCharacterDomain
import com.alexgreece45.marvelx.domain.model_api.character.toCharacterDomainList
import com.alexgreece45.marvelx.domain.model_api.comic.ComicDomain
import com.alexgreece45.marvelx.domain.model_api.comic.toComicDomain
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.core.common.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import org.koin.core.annotation.Factory

interface CharacterInteractor {
    suspend fun getAllCharacters(): Flow<GetAllCharactersPartialState>

    suspend fun getCharacter(characterId: Int): Flow<GetCharacterPartialState>

    suspend fun getCharacterComics(characterId: Int): Flow<GetCharacterComicsPartialState>

    suspend fun insertFavoriteCharacters(vararg favoriteCharacterEntity: FavoriteCharacterEntity): Flow<InsertFavoriteCharactersPartialState>

    suspend fun deleteFavoriteCharacterById(characterId: Int): Flow<DeleteFavoriteCharacterByIdPartialState>

    suspend fun getFavoriteCharacters(): Flow<GetFavoriteCharactersPartialState>

    suspend fun getFavoriteCharacterIds(): Flow<GetFavoriteCharacterIdsPartialState>

    suspend fun isCharacterFavoriteById(characterId: Int): Flow<IsCharacterFavoriteByIdPartialState>
}

@Factory
internal class CharacterInteractorImpl(
    private val characterRepository: CharacterRepository,
    private val favoriteCharactersRepository: FavoriteCharactersRepository,
    private val resourceProvider: ResourceProvider,
    private val logger: AppLogger
) : CharacterInteractor {
    override suspend fun getAllCharacters(): Flow<GetAllCharactersPartialState> =
        flow {
            characterRepository.getAllCharacters().collect {
                when (it) {
                    is GetAllCharactersRepositoryResponse.Failed -> {
                        emit(
                            GetAllCharactersPartialState.Failed(
                                errorMessage = resourceProvider.getString(R.string.error_retrieving_all_characters)
                            )
                        )
                    }

                    is GetAllCharactersRepositoryResponse.Success -> {
                        it.response?.let { characterDataWrapper ->
                            emit(
                                GetAllCharactersPartialState.Success(
                                    characters = characterDataWrapper.data.results.toCharacterDomainList()
                                )
                            )
                        } ?: emit(
                            GetAllCharactersPartialState.Failed(
                                errorMessage = resourceProvider.getString(R.string.error_retrieving_all_characters)
                            )
                        )
                    }
                }
            }
        }.safeAsync {
            logger.e { "(getAllCharacters) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> {
                    GetAllCharactersPartialState.Failed(
                        errorMessage = resourceProvider.getNoInternetConnectionMessage()
                    )
                }

                else -> {
                    GetAllCharactersPartialState.Failed(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun getCharacter(characterId: Int): Flow<GetCharacterPartialState> =
        flow {
            characterRepository.getCharacter(characterId).collect {
                when (it) {
                    is GetCharacterRepositoryResponse.Failed -> {
                        emit(
                            GetCharacterPartialState.Failed(
                                errorMessage = resourceProvider.getString(
                                    R.string.character_with_specific_id_not_found,
                                    characterId
                                )
                            )
                        )
                    }

                    is GetCharacterRepositoryResponse.Success -> {
                        it.response?.let { characterDataWrapper ->
                            if (characterDataWrapper.data.results.isNotEmpty()) {
                                emit(
                                    GetCharacterPartialState.Success(
                                        character = characterDataWrapper.data.results.first()
                                            .toCharacterDomain()
                                    )
                                )
                            } else {
                                emit(
                                    GetCharacterPartialState.Failed(
                                        errorMessage = resourceProvider.getString(
                                            R.string.character_with_specific_id_not_found,
                                            characterId
                                        )
                                    )
                                )
                            }
                        } ?: run {
                            emit(
                                GetCharacterPartialState.Failed(
                                    errorMessage = resourceProvider.getString(R.string.character_error_loading)
                                )
                            )
                        }
                    }
                }
            }
        }.safeAsync {
            when (it) {
                is IOException -> {
                    GetCharacterPartialState.Failed(
                        errorMessage = resourceProvider.getNoInternetConnectionMessage()
                    )
                }

                else -> {
                    GetCharacterPartialState.Failed(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun getCharacterComics(characterId: Int): Flow<GetCharacterComicsPartialState> =
        flow {
            characterRepository.getCharacterComics(characterId).collect {
                when (it) {
                    is GetCharacterComicsRepositoryResponse.Failed -> {
                        emit(
                            GetCharacterComicsPartialState.Failed(
                                errorMessage = resourceProvider.getString(R.string.error_retrieving_all_comics)
                            )
                        )
                    }

                    is GetCharacterComicsRepositoryResponse.Success -> {
                        it.response?.let { comicDataWrapper ->
                            emit(
                                GetCharacterComicsPartialState.Success(
                                    comics = comicDataWrapper.data.results.map { comic -> comic.toComicDomain() }
                                )
                            )
                        } ?: emit(
                            GetCharacterComicsPartialState.Failed(
                                errorMessage = resourceProvider.getString(R.string.error_retrieving_all_comics)
                            )
                        )
                    }
                }
            }
        }.safeAsync {
            logger.e { "(getCharacterComics) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> {
                    GetCharacterComicsPartialState.NoInternetConnection
                }

                else -> {
                    GetCharacterComicsPartialState.Failed(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun insertFavoriteCharacters(vararg favoriteCharacterEntity: FavoriteCharacterEntity): Flow<InsertFavoriteCharactersPartialState> =
        flow {
            val insertedCharactersSize =
                favoriteCharactersRepository.insertCharacters(favoriteCharacterEntity = favoriteCharacterEntity).size
            when (insertedCharactersSize == favoriteCharacterEntity.size) {
                true -> {
                    emit(
                        InsertFavoriteCharactersPartialState.Success
                    )
                }

                false -> {
                    emit(
                        InsertFavoriteCharactersPartialState.Failed(
                            errorMessage = resourceProvider.getString(R.string.all_characters_screen_error_favoring_character)
                        )
                    )
                }
            }
        }.safeAsync {
            logger.e { "(insertFavoriteCharacters) error: ${it.message.orEmpty()}" }
            InsertFavoriteCharactersPartialState.Failed(
                errorMessage = resourceProvider.getString(R.string.all_characters_screen_error_favoring_character)
            )
        }

    override suspend fun deleteFavoriteCharacterById(characterId: Int): Flow<DeleteFavoriteCharacterByIdPartialState> =
        flow {
            when (favoriteCharactersRepository.deleteCharacterById(characterId = characterId)) {
                1 -> {
                    emit(DeleteFavoriteCharacterByIdPartialState.Success)
                }

                else -> {
                    emit(
                        DeleteFavoriteCharacterByIdPartialState.Failed(
                            errorMessage = resourceProvider.getString(
                                R.string.all_characters_screen_error_unfavoring_character
                            )
                        )
                    )
                }
            }
        }.safeAsync {
            logger.e { "(deleteFavoriteCharacterById) error: ${it.message.orEmpty()}" }
            DeleteFavoriteCharacterByIdPartialState.Failed(
                errorMessage = resourceProvider.getString(R.string.all_characters_screen_error_favoring_character)
            )
        }

    override suspend fun getFavoriteCharacters(): Flow<GetFavoriteCharactersPartialState> =
        flow {
            emit(
                GetFavoriteCharactersPartialState.Success(
                    favoriteCharacters = favoriteCharactersRepository.getAllCharacters()
                )
            )
        }.safeAsync {
            logger.e { "(getFavoriteCharacters) error: ${it.message.orEmpty()}" }
            GetFavoriteCharactersPartialState.Failed(
                errorMessage = resourceProvider.getString(R.string.all_characters_screen_error_retrieving_favorite_characters)
            )
        }

    override suspend fun getFavoriteCharacterIds(): Flow<GetFavoriteCharacterIdsPartialState> =
        flow {
            emit(
                GetFavoriteCharacterIdsPartialState.Success(
                    favoriteCharactersRepository.getCharacterIds()
                )
            )
        }.safeAsync {
            logger.d { it.localizedMessage ?: "" }
            GetFavoriteCharacterIdsPartialState.Failed(
                errorMessage = resourceProvider.getString(R.string.all_characters_screen_error_retrieving_favorite_characters)
            )
        }

    override suspend fun isCharacterFavoriteById(characterId: Int): Flow<IsCharacterFavoriteByIdPartialState> =
        flow {
            emit(
                IsCharacterFavoriteByIdPartialState.IsFavorite(
                    favoriteCharacter = favoriteCharactersRepository.getCharacterById(characterId)
                )
            )
        }.safeAsync {
            IsCharacterFavoriteByIdPartialState.IsNotFavorite
        }
}

sealed class GetAllCharactersPartialState {
    data class Success(val characters: List<CharacterDomain>) : GetAllCharactersPartialState()
    data class Failed(val errorMessage: String) : GetAllCharactersPartialState()
}

sealed class GetCharacterPartialState {
    data class Success(val character: CharacterDomain) : GetCharacterPartialState()
    data class Failed(val errorMessage: String) : GetCharacterPartialState()
}

sealed class GetCharacterComicsPartialState {
    data class Success(val comics: List<ComicDomain>) : GetCharacterComicsPartialState()

    data class Failed(val errorMessage: String) : GetCharacterComicsPartialState()

    data object NoInternetConnection : GetCharacterComicsPartialState()
}

sealed class InsertFavoriteCharactersPartialState {
    data object Success : InsertFavoriteCharactersPartialState()

    data class Failed(val errorMessage: String) : InsertFavoriteCharactersPartialState()
}

sealed class DeleteFavoriteCharacterByIdPartialState {
    data object Success : DeleteFavoriteCharacterByIdPartialState()

    data class Failed(val errorMessage: String) : DeleteFavoriteCharacterByIdPartialState()
}

sealed class GetFavoriteCharactersPartialState {
    data class Success(val favoriteCharacters: List<FavoriteCharacterEntity>) :
        GetFavoriteCharactersPartialState()

    data class Failed(val errorMessage: String) : GetFavoriteCharactersPartialState()
}

sealed class GetFavoriteCharacterIdsPartialState {
    data class Success(val favoriteCharacterIds: List<Int>) :
        GetFavoriteCharacterIdsPartialState()

    data class Failed(val errorMessage: String) : GetFavoriteCharacterIdsPartialState()
}

sealed class IsCharacterFavoriteByIdPartialState {
    data class IsFavorite(val favoriteCharacter: FavoriteCharacterEntity) :
        IsCharacterFavoriteByIdPartialState()

    data object IsNotFavorite : IsCharacterFavoriteByIdPartialState()
}