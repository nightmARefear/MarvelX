package com.alexgreece45.marvelx.ui.screens.profile.image.model

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState

sealed interface ProfileImageContract {
    sealed interface Event : BaseEvent {
        data object OnBack : Event
        data class Init(val profileImageUrl: String) : Event

        sealed interface ProfileImage : Event {
            data object OnLoadedError : ProfileImage
            data object OnLoadedSuccess : ProfileImage
        }
    }

    data class State(
        val isLoading: Boolean = true,

        val profileImageUrl: String? = null,

        val errorView: ProfileImageErrorView? = null
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object NavigateUp : Navigation
        }
    }
}
