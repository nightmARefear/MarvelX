package com.alexgreece45.marvelx.domain.models

import androidx.compose.runtime.mutableStateListOf
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.CharacterPartialState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class CharacterEvents : BaseEvent {
    object GetAllCharacters : CharacterEvents()
}

sealed class CharacterEffects : BaseEffect {

}

data class CharacterState(
    val isLoading : Boolean = true,
    val characters : List<CharacterDomain> = mutableListOf(),
    val errorMessage : String? = null
) : BaseState

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val interactor: CharacterInteractor
): BaseViewModel<CharacterEvents, CharacterState, CharacterEffects>() {
    override fun setInitialState(): CharacterState = CharacterState()

    override suspend fun handleEvents(event: CharacterEvents) {
        when (event) {
            CharacterEvents.GetAllCharacters -> {
                setStateCompose {
                    copy(
                        isLoading = true,
                        characters = mutableListOf(),
                        errorMessage = null
                    )
                }
                interactor.getAllCharacters().collect {
                    when (it) {
                        is CharacterPartialState.Success -> {
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
                    }
                }
            }
        }
    }
}