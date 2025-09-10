package com.alexgreece45.marvelx.ui.screens.profile.image

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.navigation.NavArgHelper
import com.alexgreece45.marvelx.ui.navigation.models.NavArg
import com.alexgreece45.marvelx.ui.navigation.models.NavArgStatus
import com.alexgreece45.marvelx.ui.screens.profile.image.model.ProfileImageErrorView
import com.alexgreece45.marvelx.util.ResourceProvider
import org.koin.core.component.inject

sealed interface ProfileImageContract {
    sealed interface Event : BaseEvent {
        data object OnBack : Event
        data object Init : Event

        sealed interface ProfileImage : Event {
            data object OnLoadedError : ProfileImage
            data object OnLoadedSuccess : ProfileImage
        }
    }

    data class State(
        val isLoading: Boolean = true,

        val imageUri: String? = null,

        val errorView: ProfileImageErrorView? = null
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object NavigateUp : Navigation
        }
    }
}

class ProfileImageViewModel :
    BaseViewModel<ProfileImageContract.Event, ProfileImageContract.State, ProfileImageContract.Effect>() {

    private val resourceProvider: ResourceProvider by inject()

    override fun setInitialState(): ProfileImageContract.State =
        ProfileImageContract.State()

    override fun handleEvents(event: ProfileImageContract.Event) {
        when (event) {
            ProfileImageContract.Event.Init -> {
                when (val navArgStatus = NavArgHelper.getArgument<String>(NavArg.ProfileImageUri)) {
                    is NavArgStatus.Exists -> {
                        val imageUri = navArgStatus.value

                        if (imageUri.isBlank()) {
                            setEffect {
                                ProfileImageContract.Effect.Navigation.NavigateUp
                            }
                            return
                        }

                        setState {
                            copy(
                                imageUri = imageUri,
                            )
                        }
                    }

                    NavArgStatus.NotExists -> {
                        setEffect {
                            ProfileImageContract.Effect.Navigation.NavigateUp
                        }
                    }
                }
            }

            ProfileImageContract.Event.ProfileImage.OnLoadedError -> {
                setState {
                    copy(
                        isLoading = false,
                        errorView = ProfileImageErrorView(
                            title = resourceProvider.getString(R.string.profile_image_screen_label_error_title),
                            subtitle = resourceProvider.getString(R.string.profile_image_screen_label_error_subtitle),
                            errorButtonLayout = ErrorButtonLayout.Single(
                                primaryButtonLabel = resourceProvider.getString(R.string.profile_image_screen_label_error_button),
                                onPrimaryButtonClicked = {
                                    setEffect {
                                        ProfileImageContract.Effect.Navigation.NavigateUp
                                    }
                                }
                            )
                        )
                    )
                }
            }

            ProfileImageContract.Event.ProfileImage.OnLoadedSuccess -> {
                setState {
                    copy(
                        isLoading = false
                    )
                }
            }

            ProfileImageContract.Event.OnBack -> {
                setEffect {
                    ProfileImageContract.Effect.Navigation.NavigateUp
                }
            }
        }
    }
}
