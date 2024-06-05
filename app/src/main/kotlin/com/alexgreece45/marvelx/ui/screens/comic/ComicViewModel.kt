package com.alexgreece45.marvelx.ui.screens.comic

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.domain.model_api.comic.ComicDomain
import com.alexgreece45.marvelx.domain.interactors.ComicInteractor
import com.alexgreece45.marvelx.domain.interactors.GetComicPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import kotlinx.coroutines.launch

sealed interface ComicEvent : BaseEvent {
    data class InitView(val comicId: Int) : ComicEvent
}

data class ComicState(
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,
    val errorMessage: String? = null,

    // Data.
    val comic: ComicDomain? = null

) : BaseState

sealed interface ComicEffect : BaseEffect

class ComicViewModel(
    private val interactor: ComicInteractor
) : BaseViewModel<ComicEvent, ComicState, ComicEffect>() {
    override fun setInitialState(): ComicState =
        ComicState(
            isLoading = true,
            hasInternetConnection = true,
            errorMessage = null,
            comic = null
        )

    override fun handleEvents(event: ComicEvent) {
        when (event) {
            is ComicEvent.InitView -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                initView(comicId = event.comicId)
            }
        }
    }

    private fun initView(comicId: Int) {
        viewModelScope.launch {
            interactor.getComicById(comicId).collect { getComicPartialState ->
                when (getComicPartialState) {
                    is GetComicPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                comic = getComicPartialState.comic
                            )
                        }
                    }

                    is GetComicPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                hasInternetConnection = false
                            )
                        }
                    }

                    is GetComicPartialState.Error -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorMessage = getComicPartialState.errorMessage
                            )
                        }
                    }
                }
            }
        }
    }
}