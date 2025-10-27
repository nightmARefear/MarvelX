package com.alexgreece45.marvelx.ui.screens.profile.image

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.screens.profile.image.model.ProfileImageContract
import com.alexgreece45.marvelx.ui.screens.profile.image.model.ProfileImageErrorView
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.decodeFromUrl

class ProfileImageViewModel(
    private val resourceProvider: ResourceProvider
) :
    BaseViewModel<ProfileImageContract.Event, ProfileImageContract.State, ProfileImageContract.Effect>() {

    override fun setInitialState(): ProfileImageContract.State =
        ProfileImageContract.State()

    override fun handleEvents(event: ProfileImageContract.Event) {
        when (event) {
            is ProfileImageContract.Event.Init -> {
                val profileImageUrl = event.profileImageUrl.decodeFromUrl()
                if (profileImageUrl.isBlank()) {
                    setEffect {
                        ProfileImageContract.Effect.Navigation.NavigateUp
                    }
                    return
                }

                setState {
                    copy(
                        profileImageUrl = profileImageUrl
                    )
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
