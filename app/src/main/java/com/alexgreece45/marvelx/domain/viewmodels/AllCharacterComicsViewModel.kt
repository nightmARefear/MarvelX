package com.alexgreece45.marvelx.domain.viewmodels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.data.domains.ComicDomain
import com.alexgreece45.marvelx.data.models.character.comic.toDomain
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.GetCharacterComicsPartialState
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import com.alexgreece45.marvelx.util.SortingType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AllCharacterComicsEvent : BaseEvent {
    data class InitView(val characterId: Int) : AllCharacterComicsEvent()

    data class OnComicClick(val comicId: Int) : AllCharacterComicsEvent()

    sealed class Sorting : AllCharacterComicsEvent() {
        data class OnItemClick(val sortingType: SortingType) : Sorting()
    }

    object OnBackClick : AllCharacterComicsEvent()
}

data class AllCharacterComicsState(
    // Base States.
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,
    val errorMessage: String? = null,

    // Data.
    val comics: List<ComicDomain> = emptyList(),
    val sortingType: SortingType = SortingType.Recommended,
) : BaseState {
    companion object {
        fun getDefaultValues(): AllCharacterComicsState = AllCharacterComicsState(
            isLoading = true,
            hasInternetConnection = true,
            errorMessage = null,
            comics = emptyList()
        )
    }
}

sealed class AllCharacterComicsEffect : BaseEffect {
    object NavigateToCharacterScreen : AllCharacterComicsEffect()
    data class NavigateToComicScreen(val comicId: Int) : AllCharacterComicsEffect()
}

@HiltViewModel
class AllCharacterComicsViewModel @Inject constructor(
    private val interactor: CharacterInteractor
) : BaseViewModel<AllCharacterComicsEvent, AllCharacterComicsState, AllCharacterComicsEffect>() {
    override fun setInitialState(): AllCharacterComicsState =
        AllCharacterComicsState.getDefaultValues()

    override suspend fun handleEvents(event: AllCharacterComicsEvent) {
        when (event) {
            is AllCharacterComicsEvent.InitView -> {
                initView(characterId = event.characterId)
            }

            AllCharacterComicsEvent.OnBackClick -> {
                setEffect {
                    AllCharacterComicsEffect.NavigateToCharacterScreen
                }
            }

            is AllCharacterComicsEvent.OnComicClick -> {
                setEffect {
                    AllCharacterComicsEffect.NavigateToComicScreen(
                        comicId = event.comicId
                    )
                }
            }

            is AllCharacterComicsEvent.Sorting.OnItemClick -> {
                // We delay the update of the sorting label so that the "Check" inside the DropDownMenuItem is not displayed in a different position and the
                // UI becomes too fast. It is for aesthetic purposes only. todo: to be refactored
                delay(50L)
                when (event.sortingType) {
                    SortingType.Recommended -> {
                        // do nothing
                    }

                    SortingType.TextAscending -> {
                        setState {
                            copy(
                                sortingType = event.sortingType,
                                comics = currentState.comics.sortedBy { it.description }
                            )
                        }
                    }

                    SortingType.TextDescending -> {
                        setState {
                            copy(
                                sortingType = event.sortingType,
                                comics = currentState.comics.sortedByDescending { it.description }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun initView(characterId: Int) {
        setState {
            copy(
                isLoading = true
            )
        }

        viewModelScope.launch {
            interactor.getCharacterComics(characterId).collect { getCharacterComicsPartialState ->
                when (getCharacterComicsPartialState) {
                    is GetCharacterComicsPartialState.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorMessage = getCharacterComicsPartialState.errorMessage
                            )
                        }
                    }

                    GetCharacterComicsPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                hasInternetConnection = false
                            )
                        }
                    }

                    is GetCharacterComicsPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                comics = getCharacterComicsPartialState.comics.toDomain()
                            )
                        }
                    }
                }
            }
        }
    }
}