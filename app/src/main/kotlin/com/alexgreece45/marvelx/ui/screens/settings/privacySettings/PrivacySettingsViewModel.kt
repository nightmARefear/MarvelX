package com.alexgreece45.marvelx.ui.screens.settings.privacySettings

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.PrivacySettingsDomain
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.settings.privacySettings.model.PrivacySettingsErrorView
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

sealed interface PrivacySettingsEvent : BaseEvent {
    data object Init : PrivacySettingsEvent
    data object OnBackClicked : PrivacySettingsEvent
    data class OnFriendsListToggleClicked(val isChecked: Boolean) : PrivacySettingsEvent
    data class OnListingsListToggleClicked(val isChecked: Boolean) : PrivacySettingsEvent
    data class OnFavoritesListToggleClicked(val isChecked: Boolean) : PrivacySettingsEvent
    data class OnGenderToggleClicked(val isChecked: Boolean) : PrivacySettingsEvent
    data class OnEmailToggleClicked(val isChecked: Boolean) : PrivacySettingsEvent
}

data class PrivacySettingsState(
    val isLoading: Boolean = true,

    val privacySettings: PrivacySettingsDomain? = null,

    val errorView: PrivacySettingsErrorView? = null,
) : BaseState

sealed interface PrivacySettingsEffect : BaseEffect {
    sealed interface Navigation : PrivacySettingsEffect {
        data object PopBackStack : Navigation
    }
}

@KoinViewModel
class PrivacySettingsViewModel(
    private val userInteractor: UserInteractor,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<PrivacySettingsEvent, PrivacySettingsState, PrivacySettingsEffect>() {
    override fun setInitialState(): PrivacySettingsState =
        PrivacySettingsState()

    override fun handleEvents(event: PrivacySettingsEvent) {
        when (event) {
            PrivacySettingsEvent.Init -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                init(event)
            }

            PrivacySettingsEvent.OnBackClicked -> {
                setEffect {
                    PrivacySettingsEffect.Navigation.PopBackStack
                }
            }

            is PrivacySettingsEvent.OnFriendsListToggleClicked -> {
                currentState.privacySettings?.let {
                    updatePrivacySettings(
                        privacySettings = it.copy(
                            isFriendsListVisible = event.isChecked
                        ),
                        event = event
                    )
                }
            }

            is PrivacySettingsEvent.OnListingsListToggleClicked -> {
                currentState.privacySettings?.let {
                    updatePrivacySettings(
                        privacySettings = it.copy(
                            isListingsListVisible = event.isChecked
                        ),
                        event = event
                    )
                }
            }

            is PrivacySettingsEvent.OnFavoritesListToggleClicked -> {
                currentState.privacySettings?.let {
                    updatePrivacySettings(
                        privacySettings = it.copy(
                            isFavoritesListVisible = event.isChecked
                        ),
                        event = event
                    )
                }
            }

            is PrivacySettingsEvent.OnGenderToggleClicked -> {
                currentState.privacySettings?.let {
                    updatePrivacySettings(
                        privacySettings = it.copy(
                            isGenderVisible = event.isChecked
                        ),
                        event = event
                    )
                }
            }

            is PrivacySettingsEvent.OnEmailToggleClicked -> {
                currentState.privacySettings?.let {
                    updatePrivacySettings(
                        privacySettings = it.copy(
                            isEmailVisible = event.isChecked
                        ),
                        event = event
                    )
                }
            }
        }
    }

    /**
     * Initializes the switch values.
     */
    private fun init(event: PrivacySettingsEvent) {
        viewModelScope.launch {
            userInteractor.getPrivacySettings().collect {
                when (it) {
                    is InteractorPartialState.Error, is InteractorPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false
                            )
                        }
                        showError(
                            errorTitle = resourceProvider.getString(R.string.privacy_settings_screen_error_loading_privacy_settings),
                            errorSubtitle = resourceProvider.getGenericErrorMessage(),
                            event = event
                        )
                    }

                    InteractorPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false
                            )
                        }
                        showError(
                            errorTitle = resourceProvider.getString(R.string.privacy_settings_screen_error_loading_privacy_settings),
                            errorSubtitle = resourceProvider.getNoInternetConnectionMessage(),
                            event = event
                        )
                    }

                    is InteractorPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                privacySettings = it.data
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates the privacy settings.
     */
    private fun updatePrivacySettings(
        privacySettings: PrivacySettingsDomain,
        event: PrivacySettingsEvent
    ) {
        setState {
            copy(
                isLoading = true
            )
        }
        currentState.privacySettings?.let {
            viewModelScope.launch {
                userInteractor.updatePrivacySettings(
                    privacySettings
                ).collect { interactorPartialState ->
                    when (interactorPartialState) {
                        is InteractorPartialState.Error, is InteractorPartialState.Failed -> {
                            setState {
                                copy(
                                    isLoading = false
                                )
                            }
                            showError(
                                errorTitle = resourceProvider.getString(R.string.privacy_settings_screen_error_updating_privacy_settings),
                                errorSubtitle = resourceProvider.getGenericErrorMessage(),
                                event = event
                            )
                        }

                        InteractorPartialState.NoInternetConnection -> {
                            setState {
                                copy(
                                    isLoading = false
                                )
                            }
                            showError(
                                errorTitle = resourceProvider.getString(R.string.privacy_settings_screen_error_updating_privacy_settings),
                                errorSubtitle = resourceProvider.getNoInternetConnectionMessage(),
                                event = event
                            )
                        }

                        is InteractorPartialState.Success -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    privacySettings = privacySettings
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Shows the error with the given [errorSubtitle] as the subtitle, and [event] as the event
     * to execute when the error button is clicked.
     */
    private fun showError(
        errorTitle: String,
        errorSubtitle: String,
        event: PrivacySettingsEvent
    ) {
        setState {
            copy(
                errorView = PrivacySettingsErrorView(
                    errorTitle = errorTitle,
                    errorSubtitle = errorSubtitle,
                    primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                    event = event
                )
            )
        }
    }
}