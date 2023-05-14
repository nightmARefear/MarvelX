package com.alexgreece45.marvelx.domain.viewmodels

import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.CharacterPartialState
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class CharacterEvent : BaseEvent {
    data class LoadCharacter(val characterId: Int) : CharacterEvent()
    object OnBackClick : CharacterEvent()
}

sealed class CharacterEffect : BaseEffect {
    object OnBackClick : CharacterEffect()
}

data class CharacterState(
    val isLoading: Boolean = false,
    val hasInternetConnection: Boolean = false,
    val character: CharacterDomain? = null,
    val errorMessage: String? = null
) : BaseState

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val interactor: CharacterInteractor
) : BaseViewModel<CharacterEvent, CharacterState, CharacterEffect>() {
    override fun setInitialState(): CharacterState = CharacterState()

    /*init {
        this.setEvent(CharacterEvent.LoadCharacter(100))
    }*/

    override suspend fun handleEvents(event: CharacterEvent) {
        when (event) {
            is CharacterEvent.LoadCharacter -> {
                setState {
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
                            setState {
                                copy(
                                    isLoading = false,
                                    character = it.character
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
                                    hasInternetConnection = false
                                )
                            }
                        }
                        else -> {}
                    }
                }
            }

            is CharacterEvent.OnBackClick -> {
                setEffect {
                    CharacterEffect.OnBackClick
                }
            }
        }
    }
}