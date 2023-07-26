package com.alexgreece45.marvelx.domain.viewmodels

import com.alexgreece45.marvelx.data.domains.CharacterDomain
import com.alexgreece45.marvelx.data.domains.ComicDomain
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.CharacterPartialState
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

sealed class CharacterEvent : BaseEvent {
    data class InitView(val characterId: Int) : CharacterEvent()
    object OnBackClick : CharacterEvent()

    data class OnComicClick(val comicId: Int) : CharacterEvent()
}

sealed class CharacterEffect : BaseEffect {
    object OnBackClick : CharacterEffect()
    data class NavigateToComic(val comicId: Int) : CharacterEffect()
}

data class CharacterState(
    // Base states.
    val isLoading: Boolean = false,
    val hasInternetConnection: Boolean = false,
    val errorMessage: String? = null,

    // Data.
    val character: CharacterDomain? = null,
    val comics: List<ComicDomain> = listOf()
) : BaseState

@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val interactor: CharacterInteractor
) : BaseViewModel<CharacterEvent, CharacterState, CharacterEffect>() {
    override fun setInitialState(): CharacterState = CharacterState()

    override suspend fun handleEvents(event: CharacterEvent) {
        when (event) {
            is CharacterEvent.InitView -> {
                initView(characterId = event.characterId)
            }

            is CharacterEvent.OnBackClick -> {
                setEffect {
                    CharacterEffect.OnBackClick
                }
            }

            is CharacterEvent.OnComicClick -> {
                setEffect {
                    CharacterEffect.NavigateToComic(comicId = event.comicId)
                }
            }
        }
    }

    private suspend fun initView(characterId: Int) {
        setState {
            copy(
                isLoading = true,
                hasInternetConnection = true,
                errorMessage = null,
                character = null,
                comics = listOf()
            )
        }
        loadCharacter(characterId = characterId)
    }

    private suspend fun loadCharacter(characterId: Int) {
        coroutineScope {
            interactor.getCharacter(characterId).collect {
                when (it) {
                    is CharacterPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                character = it.data
                            )
                        }

                        // Load Comics.
                        loadComics(characterId = characterId)
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
                                hasInternetConnection = false
                            )
                        }
                    }
                }
            }
        }
    }

    private suspend fun loadComics(characterId: Int) {
        coroutineScope {
            interactor.getCharacterComics(characterId).collect {
                when (it) {
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

                    is CharacterPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                comics = it.data
                            )
                        }
                    }
                }
            }
        }
    }
}