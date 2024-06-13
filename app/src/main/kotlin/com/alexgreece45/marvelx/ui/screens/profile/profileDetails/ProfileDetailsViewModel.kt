package com.alexgreece45.marvelx.ui.screens.profile.profileDetails

import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.interactors.GetProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.UpdateProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.SnackbarState
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.helper.FieldValidationRules
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsItemUi
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsUi
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.toProfileDetailsDomain
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidationResult
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

sealed interface ProfileDetailsContract {
    /**
     * Error view data.
     */
    data class ErrorView(
        val errorTitle: String,
        val errorSubtitle: String,
        val onCloseClicked: Event,
        val errorButtonLayout: ErrorButtonLayout.Single
    )

    /**
     * Alert dialog view data.
     */
    data class AlertDialogView(
        val title: String,
        val errorMessage: String,
        val dismissButton: AlertDialogButton,
        val confirmButton: AlertDialogButton
    )

    sealed interface Event : BaseEvent {
        data object Init : Event
        data object OnBackClicked : Event

        sealed interface FirstNameField : Event {
            data class OnValueChanged(val value: String) : FirstNameField
        }

        sealed interface LastNameField : Event {
            data class OnValueChanged(val value: String) : LastNameField
        }

        sealed interface GenderField : Event {
            data object OnDismiss : GenderField
            data object OnClick : GenderField
            data class OnValueChanged(val value: Gender) : GenderField
        }

        sealed interface EmailField : Event {
            data class OnValueChanged(val value: String) : EmailField
        }

        data object OnSaveChangesButtonClicked : Event
    }

    /**
     * @param isLoading                     Whether this screen is in loading state. It has the
     * default value of `true`.
     * @param profileDetailsUi              The profile details of the user. It has the default
     * value of `null` indicating no user data.
     * @param isSaveChangesButtonEnabled    Whether the `save changes` button is enabled. It has the
     * default value of `false`.
     */
    data class State(
        val isLoading: Boolean = true,

        val isGenderDropDownExpanded: Boolean = false,
        val profileDetailsUi: ProfileDetailsUi? = null,

        val isSaveChangesButtonEnabled: Boolean = false,

        val alertDialogView: AlertDialogView? = null,
        val errorView: ErrorView? = null,
        val snackbarState: SnackbarState? = null
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object PopBackStack : Navigation
        }
    }
}

class ProfileDetailsViewModel(
    private val userInteractor: UserInteractor,
    private val fieldValidationRules: FieldValidationRules,
    private val formValidator: FormValidator,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<ProfileDetailsContract.Event, ProfileDetailsContract.State, ProfileDetailsContract.Effect>() {
    override fun setInitialState(): ProfileDetailsContract.State =
        ProfileDetailsContract.State()

    override fun handleEvents(event: ProfileDetailsContract.Event) {
        when (event) {
            ProfileDetailsContract.Event.Init -> {
                loadProfileDetails(event)
            }

            is ProfileDetailsContract.Event.FirstNameField.OnValueChanged -> {
                viewModelScope.launch {
                    validateField(
                        value = event.value,
                        validationRules = fieldValidationRules.getFirstNameValidationRules()
                    ).collect {
                        setState {
                            copy(
                                profileDetailsUi = profileDetailsUi?.copy(
                                    firstName = profileDetailsUi.firstName.copy(
                                        currentValue = event.value,
                                        validationResult = it
                                    )
                                )
                            )
                        }
                        setState {
                            copy(
                                isSaveChangesButtonEnabled = shouldEnableSaveChangesButton()
                            )
                        }
                    }
                }
            }

            is ProfileDetailsContract.Event.LastNameField.OnValueChanged -> {
                viewModelScope.launch {
                    validateField(
                        value = event.value,
                        validationRules = fieldValidationRules.getLastNameValidationRules()
                    ).collect {
                        setState {
                            copy(
                                profileDetailsUi = profileDetailsUi?.copy(
                                    lastName = profileDetailsUi.lastName.copy(
                                        currentValue = event.value,
                                        validationResult = it
                                    )
                                )
                            )
                        }
                        setState {
                            copy(
                                isSaveChangesButtonEnabled = shouldEnableSaveChangesButton()
                            )
                        }
                    }
                }
            }

            is ProfileDetailsContract.Event.GenderField.OnValueChanged -> {
                viewModelScope.launch {
                    validateField(
                        value = event.value.key,
                        validationRules = fieldValidationRules.getGenderValidationRules()
                    ).collect {
                        setState {
                            copy(
                                profileDetailsUi = profileDetailsUi?.copy(
                                    gender = profileDetailsUi.gender.copy(
                                        currentValue = event.value,
                                        validationResult = it
                                    )
                                ),
                                isGenderDropDownExpanded = !isGenderDropDownExpanded
                            )
                        }
                        setState {
                            copy(
                                isSaveChangesButtonEnabled = shouldEnableSaveChangesButton()
                            )
                        }
                    }
                }
            }

            ProfileDetailsContract.Event.GenderField.OnDismiss -> {
                setState {
                    copy(
                        isGenderDropDownExpanded = false
                    )
                }
            }

            ProfileDetailsContract.Event.GenderField.OnClick -> {
                setState {
                    copy(
                        isGenderDropDownExpanded = !isGenderDropDownExpanded
                    )
                }
            }

            is ProfileDetailsContract.Event.EmailField.OnValueChanged -> {
                viewModelScope.launch {
                    validateField(
                        value = event.value,
                        validationRules = fieldValidationRules.getEmailValidationRules()
                    ).collect {
                        setState {
                            copy(
                                profileDetailsUi = profileDetailsUi?.copy(
                                    email = profileDetailsUi.email.copy(
                                        currentValue = event.value,
                                        validationResult = it
                                    )
                                )
                            )
                        }
                        setState {
                            copy(
                                isSaveChangesButtonEnabled = shouldEnableSaveChangesButton()
                            )
                        }
                    }
                }
            }

            ProfileDetailsContract.Event.OnBackClicked -> {
                if (currentState.isSaveChangesButtonEnabled) {
                    showAlertDialogForUnsavedChanges()
                } else {
                    setEffect {
                        ProfileDetailsContract.Effect.Navigation.PopBackStack
                    }
                }
            }

            ProfileDetailsContract.Event.OnSaveChangesButtonClicked -> {
                updateProfileDetails()
            }
        }
    }

    /**
     * Loads the profile details.
     */
    private fun loadProfileDetails(event: ProfileDetailsContract.Event) {
        setState {
            copy(
                isLoading = true,
                errorView = null
            )
        }
        viewModelScope.launch {
            userInteractor.getCurrentUserProfileDetails().collect {
                when (it) {
                    is GetProfileDetailsPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorView = ProfileDetailsContract.ErrorView(
                                    errorTitle = resourceProvider.getString(R.string.profile_details_screen_error_loading_profile_details),
                                    errorSubtitle = resourceProvider.getString(R.string.internal_server_error),
                                    onCloseClicked = ProfileDetailsContract.Event.OnBackClicked,
                                    errorButtonLayout = ErrorButtonLayout.Single(
                                        primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                        onPrimaryButtonClicked = { loadProfileDetails(event) }
                                    )
                                )
                            )
                        }
                    }

                    is GetProfileDetailsPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorView = ProfileDetailsContract.ErrorView(
                                    errorTitle = resourceProvider.getString(R.string.profile_details_screen_error_loading_profile_details),
                                    errorSubtitle = resourceProvider.getNoInternetConnectionMessage(),
                                    onCloseClicked = ProfileDetailsContract.Event.OnBackClicked,
                                    errorButtonLayout = ErrorButtonLayout.Single(
                                        primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                        onPrimaryButtonClicked = { loadProfileDetails(event) }
                                    )
                                )
                            )
                        }
                    }

                    is GetProfileDetailsPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorView = null,
                                profileDetailsUi = it.profileDetailsDomain.let { profileDetailsDomain ->
                                    ProfileDetailsUi(
                                        firstName = ProfileDetailsItemUi(
                                            initialValue = profileDetailsDomain.firstName,
                                            currentValue = profileDetailsDomain.firstName,
                                            label = R.string.generic_label_first_name,
                                            onValueChanged = { value ->
                                                ProfileDetailsContract.Event.FirstNameField.OnValueChanged(
                                                    value
                                                )
                                            },
                                            onResetIconClicked = ProfileDetailsContract.Event.FirstNameField.OnValueChanged(
                                                value = profileDetailsDomain.firstName
                                            )
                                        ),
                                        lastName = ProfileDetailsItemUi(
                                            initialValue = profileDetailsDomain.lastName,
                                            currentValue = profileDetailsDomain.lastName,
                                            label = R.string.generic_label_last_name,
                                            onValueChanged = { value ->
                                                ProfileDetailsContract.Event.LastNameField.OnValueChanged(
                                                    value
                                                )
                                            },
                                            onResetIconClicked = ProfileDetailsContract.Event.LastNameField.OnValueChanged(
                                                value = profileDetailsDomain.lastName
                                            )
                                        ),
                                        gender = ProfileDetailsItemUi(
                                            initialValue = profileDetailsDomain.gender,
                                            currentValue = profileDetailsDomain.gender,
                                            label = R.string.generic_label_gender,
                                            onValueChanged = { value ->
                                                ProfileDetailsContract.Event.GenderField.OnValueChanged(
                                                    value
                                                )
                                            },
                                            onResetIconClicked = ProfileDetailsContract.Event.GenderField.OnValueChanged(
                                                value = profileDetailsDomain.gender
                                            )
                                        ),
                                        email = ProfileDetailsItemUi(
                                            initialValue = profileDetailsDomain.email,
                                            currentValue = profileDetailsDomain.email,
                                            label = R.string.generic_label_email,
                                            onValueChanged = { value ->
                                                ProfileDetailsContract.Event.EmailField.OnValueChanged(
                                                    value
                                                )
                                            },
                                            onResetIconClicked = ProfileDetailsContract.Event.EmailField.OnValueChanged(
                                                value = profileDetailsDomain.email
                                            )
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Updates the profile details.
     */
    private fun updateProfileDetails() {
        if (currentState.profileDetailsUi == null) {
            // Invalid state. Thus, display error.
            setState {
                copy(
                    errorView = ProfileDetailsContract.ErrorView(
                        errorTitle = resourceProvider.getString(R.string.profile_details_screen_error_updating_profile_details),
                        errorSubtitle = resourceProvider.getGenericErrorMessage(),
                        onCloseClicked = ProfileDetailsContract.Event.OnBackClicked,
                        errorButtonLayout = ErrorButtonLayout.Single(
                            primaryButtonLabel = resourceProvider.getString(R.string.generic_ok_button),
                            onPrimaryButtonClicked = {
                                setEffect {
                                    ProfileDetailsContract.Effect.Navigation.PopBackStack
                                }
                            }
                        )
                    )
                )
            }
            return
        }

        setState {
            copy(
                isLoading = true
            )
        }
        currentState.profileDetailsUi?.toProfileDetailsDomain()?.let { profileDetailsDomain ->
            viewModelScope.launch {
                userInteractor.updateCurrentUserProfileDetails(profileDetailsDomain).collect {
                    when (it) {
                        UpdateProfileDetailsPartialState.Success -> {
                            // TODO: Review snackbar handling.
                            setState {
                                copy(
                                    snackbarState = SnackbarState(
                                        message = resourceProvider.getString(R.string.profile_details_screen_success_updating_profile_details)
                                    )
                                )
                            }
                            setEffect {
                                ProfileDetailsContract.Effect.Navigation.PopBackStack
                            }
                        }

                        UpdateProfileDetailsPartialState.Failed -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    errorView = ProfileDetailsContract.ErrorView(
                                        errorTitle = resourceProvider.getString(R.string.profile_details_screen_error_updating_profile_details),
                                        errorSubtitle = resourceProvider.getString(R.string.internal_server_error),
                                        onCloseClicked = ProfileDetailsContract.Event.OnBackClicked,
                                        errorButtonLayout = ErrorButtonLayout.Single(
                                            primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                            onPrimaryButtonClicked = ::updateProfileDetails
                                        )
                                    )
                                )
                            }
                        }

                        UpdateProfileDetailsPartialState.NoInternetConnection -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    errorView = ProfileDetailsContract.ErrorView(
                                        errorTitle = resourceProvider.getString(R.string.profile_details_screen_error_updating_profile_details),
                                        errorSubtitle = resourceProvider.getNoInternetConnectionMessage(),
                                        onCloseClicked = ProfileDetailsContract.Event.OnBackClicked,
                                        errorButtonLayout = ErrorButtonLayout.Single(
                                            primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                            onPrimaryButtonClicked = ::updateProfileDetails
                                        )
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Validates the given [value] against the [validationRules] and emits the result as a
     * [FormValidationResult].
     */
    private suspend fun validateField(
        value: String,
        validationRules: List<Rule>
    ): Flow<FormValidationResult> = flow {
        formValidator.validateForm(Form(mapOf(validationRules to value))).collect {
            emit(it)
        }
    }

    /**
     * Checks if all the fields are valid and returns the result as a [Boolean].
     */
    private fun areAllFieldsValid(): Boolean {
        if (currentState.profileDetailsUi?.firstName?.validationResult?.isValid == false) return false
        if (currentState.profileDetailsUi?.lastName?.validationResult?.isValid == false) return false
        if (currentState.profileDetailsUi?.gender?.validationResult?.isValid == false) return false
        if (currentState.profileDetailsUi?.email?.validationResult?.isValid == false) return false

        return true
    }

    /**
     * Calculates whether the `save changes` button should be enabled based on whether the fields'
     * values have changed, and if they have, it checks if the validations have been passed. The
     * result is returned as a [Boolean].
     */
    private fun shouldEnableSaveChangesButton(): Boolean {
        if (!areAllFieldsValid()) return false

        if (currentState.profileDetailsUi?.firstName?.initialValue == currentState.profileDetailsUi?.firstName?.currentValue
            && currentState.profileDetailsUi?.lastName?.initialValue == currentState.profileDetailsUi?.lastName?.currentValue
            && currentState.profileDetailsUi?.gender?.initialValue == currentState.profileDetailsUi?.gender?.currentValue
            && currentState.profileDetailsUi?.email?.initialValue == currentState.profileDetailsUi?.email?.currentValue
        ) return false

        return true
    }

    /**
     * Displays the alert dialog when the user tries to exit the screen while they have unsaved
     * changes.
     */
    private fun showAlertDialogForUnsavedChanges() {
        setState {
            copy(
                alertDialogView = ProfileDetailsContract.AlertDialogView(
                    title = resourceProvider.getString(R.string.profile_details_screen_alert_dialog_unsaved_changes_title),
                    errorMessage = resourceProvider.getString(R.string.profile_details_screen_alert_dialog_unsaved_changes_subtitle),
                    dismissButton = AlertDialogButton(
                        value = resourceProvider.getString(R.string.generic_no_button),
                        onClick = {
                            setState {
                                copy(
                                    alertDialogView = null
                                )
                            }
                        }
                    ),
                    confirmButton = AlertDialogButton(
                        value = resourceProvider.getString(R.string.generic_yes_button),
                        buttonType = ErrorButtonType.Contained,
                        onClick = {
                            setEffect {
                                ProfileDetailsContract.Effect.Navigation.PopBackStack
                            }
                        }
                    )
                )
            )
        }
    }
}