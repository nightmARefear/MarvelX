package com.alexgreece45.marvelx.ui.screens.comic

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.domain.interactors.ComicInteractor
import com.alexgreece45.marvelx.domain.interactors.GetComicPartialState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.comic.model.ComicContract
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ComicViewModel(
    private val interactor: ComicInteractor
) : BaseViewModel<ComicContract.Event, ComicContract.State, ComicContract.Effect>() {
    override fun setInitialState(): ComicContract.State =
        ComicContract.State()

    override fun handleEvents(event: ComicContract.Event) {
        when (event) {
            is ComicContract.Event.Init -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                initView(comicId = event.comicId)
            }

            ComicContract.Event.OnBackClicked -> {
                setEffect {
                    ComicContract.Effect.PopBackStack
                }
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