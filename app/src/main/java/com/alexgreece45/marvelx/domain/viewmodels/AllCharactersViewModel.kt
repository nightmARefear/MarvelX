package com.alexgreece45.marvelx.domain.viewmodels

import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.CharacterPartialState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class AllCharactersEvent : BaseEvent {
    object LoadAllCharacters : AllCharactersEvent()
    data class LoadCharacter(val characterId: Int) : AllCharactersEvent()
}

sealed class AllCharactersEffect : BaseEffect {
    data class NavigateToCharacterDetails(val characterId: Int) : AllCharactersEffect()
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
    private val interactor: CharacterInteractor
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
                            setState {
                                copy(
                                    isLoading = false,
                                    characters = it.results
                                )
                            }
                        }
                        is CharacterPartialState.Failed -> {
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