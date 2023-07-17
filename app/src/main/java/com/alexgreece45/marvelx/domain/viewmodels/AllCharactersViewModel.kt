package com.alexgreece45.marvelx.domain.viewmodels

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.domain.database.FavoriteCharacterEntity
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.CharacterPartialState
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import com.alexgreece45.marvelx.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed interface AllCharactersEvent : BaseEvent {
    object InitView : AllCharactersEvent
    data class OnCharacterClick(val characterId: Int) : AllCharactersEvent
    data class OnHeartClick(val characterId: Int) : AllCharactersEvent
}

sealed interface AllCharactersEffect : BaseEffect {
    data class NavigateToCharacterDetails(val characterId: Int) : AllCharactersEffect
    sealed interface FavoriteCharacter : AllCharactersEffect {
        data class Success(val successMessage: String) : FavoriteCharacter
        data class Error(val errorMessage: String) : FavoriteCharacter
    }

    sealed interface UnFavoriteCharacter : AllCharactersEffect {
        data class Success(val successMessage: String) : UnFavoriteCharacter
        data class Error(val errorMessage: String) : UnFavoriteCharacter
    }
}

data class AllCharactersState(
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = false,
    val characters: List<CharacterDomain> = mutableListOf(),
    val errorMessage: String? = null
) : BaseState

@HiltViewModel
class AllCharactersViewModel @Inject constructor(
    private val interactor: CharacterInteractor,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<AllCharactersEvent, AllCharactersState, AllCharactersEffect>() {
    override fun setInitialState(): AllCharactersState = AllCharactersState()

    init {
        setEvent(AllCharactersEvent.InitView)
    }

    override suspend fun handleEvents(event: AllCharactersEvent) {
        when (event) {
            AllCharactersEvent.InitView -> {
                setState {
                    copy(
                        isLoading = true,
                        characters = mutableListOf(),
                        errorMessage = null
                    )
                }
                interactor.getAllCharacters().collect {
                    when (it) {
                        is CharacterPartialState.Success -> {
//                            delay(2000L) // todo: to be removed
                            // Load saved characters.
                            interactor.getFavoriteCharacters()
                                .collect { favoriteCharacterPartialState ->
                                    when (favoriteCharacterPartialState) {
                                        is CharacterPartialState.Success -> {
                                            val newCharacterList =
                                                it.data.map { character ->
                                                    character.copy(isFavorite = favoriteCharacterPartialState.data.firstOrNull { favoriteCharacterEntity ->
                                                        favoriteCharacterEntity.characterId == character.id
                                                    }?.favorite ?: false)
                                                }

                                            setState {
                                                copy(
                                                    isLoading = false,
                                                    characters = newCharacterList
                                                )
                                            }
                                        }

                                        else -> {
                                            setState {
                                                copy(
                                                    isLoading = false,
                                                    characters = it.data
                                                )
                                            }
                                        }
                                    }
                                }
                        }

                        is CharacterPartialState.Error -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = it.errorMessage
                                )
                            }
                        }

                        is CharacterPartialState.NoInternetConnection -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    errorMessage = it.errorMessage
                                )
                            }
                        }
                    }
                }
            }

            is AllCharactersEvent.OnCharacterClick -> {
                setState {
                    copy(
                        isLoading = false, // todo: change later
                        hasInternetConnection = true,
                        errorMessage = null
                    )
                }
                setEffect {
                    AllCharactersEffect.NavigateToCharacterDetails(
                        characterId = event.characterId
                    )
                }

            }

            is AllCharactersEvent.OnHeartClick -> {
                onHeartClick(characterId = event.characterId)
            }
        }
    }

    private suspend fun onHeartClick(characterId: Int) {
        // Retrieve character.
        val character = currentState.characters.firstOrNull { it.id == characterId }
        character?.let {
            // Check if character is favorite.
            when (character.isFavorite) {
                true -> {
                    // Character is favorite, so unfavorite it.
                    unFavoriteCharacterById(characterId = characterId)
                }

                false -> {
                    // Character is not favorite, so make it.
                    favoriteCharacterById(characterId = characterId)
                }
            }
        } ?: {
            setEffect {
                AllCharactersEffect.FavoriteCharacter.Error(
                    errorMessage = resourceProvider.getString(R.string.unknown_error)
                )
            }
        }
    }

    private suspend fun unFavoriteCharacterById(characterId: Int) {
        interactor.deleteFavoriteCharacterById(characterId = characterId).collect {
            when (it) {
                is CharacterPartialState.Success -> {
                    setState {
                        copy(
                            characters = currentState.characters.map { characterDomain ->
                                if (characterDomain.id == characterId) characterDomain.copy(
                                    isFavorite = false
                                ) else characterDomain
                            }
                        )
                    }
                    setEffect {
                        AllCharactersEffect.UnFavoriteCharacter.Success(
                            successMessage = resourceProvider.getString(R.string.all_characters_screen_success_unfavoring_character)
                        )
                    }
                }

                is CharacterPartialState.Error -> {
                    setEffect {
                        AllCharactersEffect.UnFavoriteCharacter.Error(
                            errorMessage = it.errorMessage
                        )
                    }
                }

                is CharacterPartialState.NoInternetConnection -> {
                    setState {
                        copy(
                            hasInternetConnection = false
                        )
                    }
                }
            }
        }
    }

    private suspend fun favoriteCharacterById(characterId: Int) {
        interactor.insertFavoriteCharacters(
            FavoriteCharacterEntity(
                characterId = characterId,
                favorite = true
            )
        ).collect {
            when (it) {
                is CharacterPartialState.Success -> {
                    setState {
                        copy(
                            characters = currentState.characters.map { characterDomain ->
                                if (characterDomain.id == characterId) characterDomain.copy(
                                    isFavorite = true
                                ) else characterDomain
                            }
                        )
                    }
                    setEffect {
                        AllCharactersEffect.UnFavoriteCharacter.Success(
                            successMessage = resourceProvider.getString(R.string.all_characters_screen_success_favoring_character)
                        )
                    }
                }

                is CharacterPartialState.Error -> setEffect {
                    AllCharactersEffect.FavoriteCharacter.Error(errorMessage = it.errorMessage)
                }

                is CharacterPartialState.NoInternetConnection -> {
                    setState {
                        copy(
                            hasInternetConnection = false
                        )
                    }
                }
            }
        }
    }
}