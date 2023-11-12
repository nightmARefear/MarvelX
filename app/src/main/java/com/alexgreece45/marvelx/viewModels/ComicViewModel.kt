package com.alexgreece45.marvelx.viewModels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.data.domains.comic.ComicDomain
import com.alexgreece45.marvelx.interactors.ComicInteractor
import com.alexgreece45.marvelx.interactors.GetComicPartialState
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ComicEvent : BaseEvent {
    data class InitView(val comicId: Int) : ComicEvent
}

data class ComicState(
    val isLoading: Boolean = true,
    val hasInternetConnection: Boolean = true,
    val errorMessage: String? = null,

    // Data.
    val comic: ComicDomain? = null

) : BaseState {
    companion object {
        fun getDefaultValues(): ComicState =
            ComicState(
                isLoading = true,
                hasInternetConnection = true,
                errorMessage = null,
                comic = null
            )
    }
}

sealed interface ComicEffect : BaseEffect

@HiltViewModel
class ComicViewModel @Inject constructor(
    private val interactor: ComicInteractor
) : BaseViewModel<ComicEvent, ComicState, ComicEffect>() {
    override fun setInitialState(): ComicState = ComicState()

    override fun handleEvents(event: ComicEvent) {
        when (event) {
            is ComicEvent.InitView -> {
                initView(comicId = event.comicId)
            }
        }
    }

    private fun initView(comicId: Int) {
        setState {
            ComicState.getDefaultValues()
        }

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