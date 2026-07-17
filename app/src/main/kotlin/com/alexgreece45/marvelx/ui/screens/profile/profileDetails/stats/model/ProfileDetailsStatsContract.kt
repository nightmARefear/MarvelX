package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model

import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState

sealed interface ProfileDetailsStatsContract {

    sealed interface Event : BaseEvent {

        data class Init(val fullName: String, val userId: String, val tabIndex: Int) : Event
        data object OnBackClicked : Event

        data class OnItemClicked(val id: String) : Event

        data object OnErrorCloseButtonClicked : Event
    }

    data class State(
        val isLoading: Boolean = true,

        val userId: UserId = UserId.EMPTY,
        val selectedTab: ProfileDetailsStatsTab = ProfileDetailsStatsTab.getFirstTab(),
        val items: List<ProfileDetailsStatsItem> = emptyList(),

        val error: ProfileDetailsStatsErrorView? = null,
    ) : BaseState

    sealed interface Effect : BaseEffect {

        sealed interface Navigation : Effect {
            data object NavigateUp : Navigation
            data class NavigateToProfileDetailsScreen(val userId: String) : Navigation
            data class NavigateToListingScreen(val listingId: String) : Navigation
            data class NavigateToCharacterScreen(val characterId: Int) : Navigation
        }
    }
}
