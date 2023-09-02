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

    object OnDropDownArrowClick : AllCharacterComicsEvent()

    sealed class Sorting : AllCharacterComicsEvent() {
        data class OnItemClick(val sortingType: SortingType) : Sorting()
        object OnCloseClick : Sorting()
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
    val sortingType: SortingType = SortingType.getDefaultSortingType(),
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

    sealed class BottomSheet : AllCharacterComicsEffect() {
        object Show : BottomSheet()
        object Hide : BottomSheet()
    }
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
                when (event.sortingType) {
                    SortingType.Random -> {
                        setEffect {
                            AllCharacterComicsEffect.BottomSheet.Hide
                        }

                        // Stops from re-ordering the list of items if the sortingType is the same.
                        if (event.sortingType == currentState.sortingType) {
                            return
                        }

                        setState {
                            copy(
                                sortingType = event.sortingType,
                                comics = currentState.comics.shuffled()
                            )
                        }
                    }

                    SortingType.TextAscending -> {
                        setEffect {
                            AllCharacterComicsEffect.BottomSheet.Hide
                        }

                        // Stops from re-ordering the list of items if the sortingType is the same.
                        if (event.sortingType == currentState.sortingType) {
                            return
                        }

                        setState {
                            copy(
                                sortingType = event.sortingType,
                                comics = currentState.comics.sortedBy { it.description }
                            )
                        }
                    }

                    SortingType.TextDescending -> {
                        setEffect {
                            AllCharacterComicsEffect.BottomSheet.Hide
                        }

                        // Stops from re-ordering the list of items if the sortingType is the same.
                        if (event.sortingType == currentState.sortingType) {
                            return
                        }

                        setState {
                            copy(
                                sortingType = event.sortingType,
                                comics = currentState.comics.sortedByDescending { it.description }
                            )
                        }
                    }
                }
            }

            AllCharacterComicsEvent.Sorting.OnCloseClick -> {
                setEffect {
                    AllCharacterComicsEffect.BottomSheet.Hide
                }
            }

            AllCharacterComicsEvent.OnDropDownArrowClick -> {
                setEffect {
                    AllCharacterComicsEffect.BottomSheet.Show
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