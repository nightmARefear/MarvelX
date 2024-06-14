package com.alexgreece45.marvelx.ui.screens.profile.privacySettings

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.PrivacySettingsDomain
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.screens.profile.privacySettings.model.PrivacySettingsErrorView
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.launch

sealed interface PrivacySettingsEvent : BaseEvent {
    data object Init : PrivacySettingsEvent
    data object OnBackClicked : PrivacySettingsEvent
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

            is PrivacySettingsEvent.OnGenderToggleClicked -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                updatePrivacySettings(
                    isGenderVisible = event.isChecked,
                    isEmailVisible = currentState.privacySettings?.isEmailVisible ?: false,
                    event = event
                )
            }

            is PrivacySettingsEvent.OnEmailToggleClicked -> {
                setState {
                    copy(
                        isLoading = true
                    )
                }
                updatePrivacySettings(
                    isGenderVisible = currentState.privacySettings?.isGenderVisible ?: false,
                    isEmailVisible = event.isChecked,
                    event = event
                )
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
                        showError(resourceProvider.getGenericErrorMessage(), event)
                    }

                    InteractorPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false
                            )
                        }
                        showError(resourceProvider.getNoInternetConnectionMessage(), event)
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
     * Updates the privacy settings based on the given [isGenderVisible] and [isEmailVisible].
     */
    private fun updatePrivacySettings(
        isGenderVisible: Boolean,
        isEmailVisible: Boolean,
        event: PrivacySettingsEvent
    ) {
        currentState.privacySettings?.let {
            val finalPrivacySettings = it.copy(
                isGenderVisible = isGenderVisible,
                isEmailVisible = isEmailVisible
            )
            viewModelScope.launch {
                userInteractor.updatePrivacySettings(
                    finalPrivacySettings
                ).collect { interactorPartialState ->
                    when (interactorPartialState) {
                        is InteractorPartialState.Error, is InteractorPartialState.Failed -> {
                            setState {
                                copy(
                                    isLoading = false
                                )
                            }
                            showError(resourceProvider.getGenericErrorMessage(), event)
                        }

                        InteractorPartialState.NoInternetConnection -> {
                            setState {
                                copy(
                                    isLoading = false
                                )
                            }
                            showError(resourceProvider.getNoInternetConnectionMessage(), event)
                        }

                        is InteractorPartialState.Success -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    privacySettings = finalPrivacySettings
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
    private fun showError(errorSubtitle: String, event: PrivacySettingsEvent) {
        setState {
            copy(
                errorView = PrivacySettingsErrorView(
                    errorSubtitle = errorSubtitle,
                    primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                    event = event
                )
            )
        }
    }
}