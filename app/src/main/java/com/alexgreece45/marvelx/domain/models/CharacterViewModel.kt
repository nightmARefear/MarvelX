package com.alexgreece45.marvelx.domain.models

import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.CharacterPartialState
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import javax.inject.Inject

sealed class CharacterEvent : BaseEvent {
    data class LoadCharacter(val characterId: Int) : CharacterEvent()
}

sealed class CharacterEffect : BaseEffect {}

data class CharacterState(
    val isLoading: Boolean = false,
    val hasInternetConnection: Boolean = false,
    val character: CharacterDomain? = null,
    val errorMessage: String? = null
) : BaseState

class CharacterViewModel @Inject constructor(
    private val interactor: CharacterInteractor
) : BaseViewModel<CharacterEvent, CharacterState, CharacterEffect>() {
    override fun setInitialState(): CharacterState = CharacterState()

    override suspend fun handleEvents(event: CharacterEvent) {
        when (event) {
            is CharacterEvent.LoadCharacter -> {
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
}