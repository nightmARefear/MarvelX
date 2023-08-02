package com.alexgreece45.marvelx.domain.viewmodels

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.data.domains.ComicDomain
import com.alexgreece45.marvelx.domain.interactors.ComicInteractor
import com.alexgreece45.marvelx.domain.interactors.GetComicPartialState
import com.alexgreece45.marvelx.ui.base.BaseEffect
import com.alexgreece45.marvelx.ui.base.BaseEvent
import com.alexgreece45.marvelx.ui.base.BaseState
import com.alexgreece45.marvelx.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ComicEvent : BaseEvent {
    data class InitView(val comicId: Int) : ComicEvent
}

data class ComicState(
    val isLoading: Boolean = false,
    val hasInternetConnection: Boolean = true,
    val errorMessage: String? = null,

    // Data.
    val comic: ComicDomain? = null

) : BaseState

sealed interface ComicEffect : BaseEffect {

}

class ComicViewModel @Inject constructor(
    private val interactor: ComicInteractor
) : BaseViewModel<ComicEvent, ComicState, ComicEffect>() {
    override fun setInitialState(): ComicState = ComicState()

    override suspend fun handleEvents(event: ComicEvent) {
        when (event) {
            is ComicEvent.InitView -> {
                initView(comicId = event.comicId)
            }
        }
    }

    private fun initView(comicId: Int) {
        setState {
            copy(
                isLoading = true
            )
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