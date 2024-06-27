package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats

import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model.ProfileDetailsStatsTab
import com.alexgreece45.marvelx.util.ResourceProvider

sealed interface ProfileDetailsStatsContract {
    sealed interface Event : BaseEvent {
        data object OnBackClicked : Event
        data class Init(val fullName: String, val userId: Int, val tabIndex: Int) : Event
        data class OnTabClicked(val tab: ProfileDetailsStatsTab) : Event
    }

    data class State(
        val isLoading: Boolean = true,
        val tabLoading: Boolean = false,

        val selectedTab: ProfileDetailsStatsTab = ProfileDetailsStatsTab.getFirstTab(),
        val tabs: List<ProfileDetailsStatsTab> = ProfileDetailsStatsTab.getTabs()
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object NavigateUp : Navigation
        }
    }
}

class ProfileDetailsStatsViewModel(
    private val resourceProvider: ResourceProvider
) :
    BaseViewModel<ProfileDetailsStatsContract.Event, ProfileDetailsStatsContract.State, ProfileDetailsStatsContract.Effect>() {
    override fun setInitialState(): ProfileDetailsStatsContract.State =
        ProfileDetailsStatsContract.State()

    override fun handleEvents(event: ProfileDetailsStatsContract.Event) {
        when (event) {
            is ProfileDetailsStatsContract.Event.Init -> {
                val finalTab =
                    if (currentState.tabs.firstOrNull { it.tabIndex == event.tabIndex } != null) currentState.tabs[event.tabIndex] else currentState.tabs.first()
                setState {
                    copy(
                        isLoading = true,
                        selectedTab = finalTab
                    )
                }
            }

            ProfileDetailsStatsContract.Event.OnBackClicked -> {
                setEffect {
                    ProfileDetailsStatsContract.Effect.Navigation.NavigateUp
                }
            }

            is ProfileDetailsStatsContract.Event.OnTabClicked -> {
                if (event.tab == currentState.selectedTab) {
                    return
                }

                setState {
                    copy(
                        tabLoading = true,
                        selectedTab = event.tab
                    )
                }
            }
        }
    }
}
