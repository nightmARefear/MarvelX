package com.alexgreece45.marvelx.domain.viewmodels

import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.CharacterPartialState
import com.alexgreece45.marvelx.util.ResourceProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

sealed interface AllCharactersEvent : BaseEvent {
    object LoadAllCharacters : AllCharactersEvent
    data class LoadCharacter(val characterId: Int) : AllCharactersEvent
    data class OnFavoriteCharacterClick(val characterName: String): AllCharactersEvent
}

sealed interface AllCharactersEffect : BaseEffect {
    data class NavigateToCharacterDetails(val characterId: Int) : AllCharactersEffect
    sealed interface FavoriteCharacter : AllCharactersEffect {
        data class Success(val successMessage: String) : FavoriteCharacter
        data class Failed(val errorMessage: String) : FavoriteCharacter
    }
}

data class AllCharactersState(
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = false,
    val characters: List<CharacterDomain> = mutableListOf(),
    val character: CharacterDomain? = null,
    val errorMessage: String? = null
) : BaseState

@HiltViewModel
class AllCharactersViewModel @Inject constructor(
    private val interactor: CharacterInteractor,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<AllCharactersEvent, AllCharactersState, AllCharactersEffect>() {
    override fun setInitialState(): AllCharactersState = AllCharactersState()

    init {
        setEvent(AllCharactersEvent.LoadAllCharacters)
    }

    override suspend fun handleEvents(event: AllCharactersEvent) {
        when (event) {
            AllCharactersEvent.LoadAllCharacters -> {
                setState {
                    copy(
                        isLoading = true,
                        characters = mutableListOf(),
                        character = null,
                        errorMessage = null
                    )
                }
                interactor.getAllCharacters().collect {
                    when (it) {
                        is CharacterPartialState.SuccessAllCharacters -> {
                            delay(2000L)
                            setState {
                                copy(
                                    isLoading = false,
                                    characters = it.results
                                )
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
                        else -> {}
                    }
                }
            }
            is AllCharactersEvent.LoadCharacter -> {
                setState {
                    copy(
                        isLoading = false, // todo: change later
                        hasInternetConnection = true,
                        character = null,
                        errorMessage = null
                    )
                }
                setEffect {
                    AllCharactersEffect.NavigateToCharacterDetails(
                        characterId = event.characterId
                    )
                }

            }

            is AllCharactersEvent.OnFavoriteCharacterClick -> {
                // todo: favorite character
                setEffect {
                    AllCharactersEffect.FavoriteCharacter.Success(event.characterName)
                }
            }
        }
    }

    fun setCharacterToNull() {
        setState {
            copy(
                character = null
            )
        }
    }
}