package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.network.model.api.user.friend.ResponseFriend
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.user.FriendRepository
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model.ProfileDetailsStatsContract
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model.ProfileDetailsStatsErrorView
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model.ProfileDetailsStatsTab
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.model.toProfileDetailsStatsItem
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class ProfileDetailsStatsViewModel(
    private val resourceProvider: ResourceProvider,
    private val friendRepository: FriendRepository
) :
    BaseViewModel<ProfileDetailsStatsContract.Event, ProfileDetailsStatsContract.State, ProfileDetailsStatsContract.Effect>() {

    override fun setInitialState(): ProfileDetailsStatsContract.State =
        ProfileDetailsStatsContract.State()

    override fun handleEvents(event: ProfileDetailsStatsContract.Event) {
        when (event) {
            is ProfileDetailsStatsContract.Event.Init -> {
                val allTabs = ProfileDetailsStatsTab.getTabs()
                val finalTab =
                    if (allTabs.firstOrNull { it.tabIndex == event.tabIndex } != null) allTabs[event.tabIndex] else allTabs.first()

                setState {
                    copy(
                        userId = event.userId,
                        selectedTab = finalTab
                    )
                }

                loadData()
            }

            ProfileDetailsStatsContract.Event.OnBackClicked -> {
                setEffect {
                    ProfileDetailsStatsContract.Effect.Navigation.NavigateUp
                }
            }

            is ProfileDetailsStatsContract.Event.OnItemClicked -> {
                when (currentState.selectedTab) {
                    ProfileDetailsStatsTab.Friends -> {
                        setEffect {
                            ProfileDetailsStatsContract.Effect.Navigation.NavigateToProfileDetailsScreen(
                                userId = event.id
                            )
                        }
                    }

                    ProfileDetailsStatsTab.Listings -> {
                        setEffect {
                            ProfileDetailsStatsContract.Effect.Navigation.NavigateToListingScreen(
                                listingId = event.id
                            )
                        }
                    }

                    ProfileDetailsStatsTab.Favorites -> {
                        setEffect {
                            ProfileDetailsStatsContract.Effect.Navigation.NavigateToCharacterScreen(
                                characterId = event.id.toInt()
                            )
                        }
                    }
                }
            }

            ProfileDetailsStatsContract.Event.OnErrorCloseButtonClicked -> {
                setEffect {
                    ProfileDetailsStatsContract.Effect.Navigation.NavigateUp
                }
            }
        }
    }

    private fun loadData() {
        setState {
            copy(
                isLoading = true,
                error = null
            )
        }

        when (currentState.selectedTab) {
            ProfileDetailsStatsTab.Friends -> getFriends()
            ProfileDetailsStatsTab.Listings -> getListings()
            ProfileDetailsStatsTab.Favorites -> getFavorites()
        }
    }

    private fun getFriends() {
        viewModelScope.launch {
            friendRepository.getFriends(currentState.userId).collect { repositoryResult ->
                when (repositoryResult) {
                    is RepositoryResult.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = ProfileDetailsStatsErrorView(
                                    subtitle = resourceProvider.getString(R.string.generic_unknown_error),
                                    primaryButtonLabel = resourceProvider.getString(R.string.generic_go_back),
                                    primaryButtonOnClicked = {
                                        setEffect {
                                            ProfileDetailsStatsContract.Effect.Navigation.NavigateUp
                                        }
                                    }
                                )
                            )
                        }
                    }

                    RepositoryResult.NetworkError -> {
                        setState {
                            copy(
                                isLoading = false,
                                error = ProfileDetailsStatsErrorView(
                                    subtitle = resourceProvider.getString(R.string.no_internet_connection_error_message),
                                    primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                    primaryButtonOnClicked = ::loadData
                                )
                            )
                        }
                    }

                    is RepositoryResult.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                items = repositoryResult.data.map(ResponseFriend::toProfileDetailsStatsItem)
                            )
                        }
                    }
                }
            }
        }
    }

    private fun getListings() {
        setState {
            copy(
                isLoading = false,
                error = ProfileDetailsStatsErrorView(
                    subtitle = resourceProvider.getString(R.string.generic_unknown_error),
                    primaryButtonLabel = resourceProvider.getString(R.string.generic_go_back),
                    primaryButtonOnClicked = {
                        setEffect {
                            ProfileDetailsStatsContract.Effect.Navigation.NavigateUp
                        }
                    }
                )
            )
        }
    }

    private fun getFavorites() {
        setState {
            copy(
                isLoading = false,
                error = ProfileDetailsStatsErrorView(
                    subtitle = resourceProvider.getString(R.string.generic_unknown_error),
                    primaryButtonLabel = resourceProvider.getString(R.string.generic_go_back),
                    primaryButtonOnClicked = {
                        setEffect {
                            ProfileDetailsStatsContract.Effect.Navigation.NavigateUp
                        }
                    }
                )
            )
        }
    }
}