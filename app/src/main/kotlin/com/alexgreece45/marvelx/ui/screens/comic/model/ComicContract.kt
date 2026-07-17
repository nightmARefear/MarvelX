package com.alexgreece45.marvelx.ui.screens.comic.model

import com.alexgreece45.marvelx.domain.model_api.comic.ComicDomain
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState

sealed interface ComicContract {
    sealed interface Event : BaseEvent {
        data class Init(val comicId: Int) : Event
        data object OnBackClicked : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val hasInternetConnection: Boolean = true,
        val errorMessage: String? = null,

        // Data.
        val comic: ComicDomain? = null

    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object GoBack : Navigation
        }
    }
}