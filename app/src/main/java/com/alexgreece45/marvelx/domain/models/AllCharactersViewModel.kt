package com.alexgreece45.marvelx.domain.models

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
    object GetAllCharacters : AllCharactersEvent()
    data class LoadCharacter(val characterId: Int) : AllCharactersEvent()
}

sealed class AllCharactersEffect : BaseEffect {
    data class ShowCharacterDetails(val characterId: Int) : AllCharactersEffect()
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

    override suspend fun handleEvents(event: AllCharactersEvent) {
        when (event) {
            AllCharactersEvent.GetAllCharacters -> {
                setStateCompose {
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
                            setStateCompose {
                                copy(
                                    isLoading = false,
                                    characters = it.results
                                )
                            }
                        }
                        is CharacterPartialState.Failed -> {
                            setStateCompose {
                                copy(
                                    isLoading = false,
                                    errorMessage = it.errorMessage
                                )
                            }
                        }
                        is CharacterPartialState.NoInternetConnection -> {
                            setStateCompose {
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
//                setEffect {
//                    AllCharactersEffect.ShowCharacterDetails(
//                        event.characterId
//                    )
//                }
                setStateCompose {
                    copy(
                        isLoading = true,
                        hasInternetConnection = true,
                        character = null,
                        errorMessage = null
                    )
                }
                interactor.getCharacter(event.characterId).collect {
                    when (it) {
                        is CharacterPartialState.SuccessCharacter -> {
                            setStateCompose {
                                copy(
                                    isLoading = false,
                                    character = it.character
                                )
                            }
                        }
                        is CharacterPartialState.Failed -> {
                            setStateCompose {
                                copy(
                                    isLoading = false,
                                    errorMessage = it.errorMessage
                                )
                            }
                        }
                        is CharacterPartialState.NoInternetConnection -> {
                            setStateCompose {
                                copy(
                                    isLoading = false,
                                    hasInternetConnection = false
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun setCharacterToNull() {
        setStateCompose {
            copy(
                character = null
            )
        }
    }
}