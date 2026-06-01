package com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.database.toCharacterCardUiList
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.GetFavoriteCharactersPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.allCharacters.uiModel.CharacterCardUi
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.model.AllFavoriteCharactersScreenErrorView
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

sealed class AllFavoriteCharactersEvent : BaseEvent {
    data object Init : AllFavoriteCharactersEvent()
}

data class AllFavoriteCharactersState(
    val isLoading: Boolean = false,
    val error: AllFavoriteCharactersScreenErrorView? = null,

    // Data.
    val favoriteCharacters: List<CharacterCardUi> = emptyList()
) : BaseState

sealed class AllFavoriteCharactersEffect : BaseEffect

@KoinViewModel
class AllFavoriteCharactersViewModel(
    private val characterInteractor: CharacterInteractor
) : BaseViewModel<AllFavoriteCharactersEvent, AllFavoriteCharactersState, AllFavoriteCharactersEffect>() {
    override fun setInitialState(): AllFavoriteCharactersState =
        AllFavoriteCharactersState(
            isLoading = true,
            error = null,
            favoriteCharacters = emptyList()
        )

    override fun handleEvents(event: AllFavoriteCharactersEvent) {
        when (event) {
            AllFavoriteCharactersEvent.Init -> {
                initView()
            }
        }
    }

    private fun initView() {
        setState {
            copy(
                isLoading = true
            )
        }
        viewModelScope.launch {
            characterInteractor.getFavoriteCharacters().collect {
                when (it) {
                    is GetFavoriteCharactersPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                favoriteCharacters = it.favoriteCharacters.toCharacterCardUiList()
                            )
                        }
                    }

                    is GetFavoriteCharactersPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = AllFavoriteCharactersScreenErrorView(
                                    errorMessage = it.errorMessage,
                                    onRetryClick = { initView() }
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
