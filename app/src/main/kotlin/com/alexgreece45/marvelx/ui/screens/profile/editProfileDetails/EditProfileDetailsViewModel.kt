package com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails

import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewModelScope
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.interactors.GetCurrentUserProfileDetailsPartialState
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.mvi.BaseEffect
import com.alexgreece45.marvelx.mvi.BaseEvent
import com.alexgreece45.marvelx.mvi.BaseState
import com.alexgreece45.marvelx.mvi.BaseViewModel
import com.alexgreece45.marvelx.ui.components.AlertDialogButton
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonLayout
import com.alexgreece45.marvelx.ui.components.model.ErrorButtonType
import com.alexgreece45.marvelx.ui.components.model.SnackbarStateData
import com.alexgreece45.marvelx.ui.helper.FieldValidationRules
import com.alexgreece45.marvelx.ui.model.Gender
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.EditProfileDetailsItemUi
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.ProfileDetailsUi
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model.toBasicProfileDetailsDomain
import com.alexgreece45.marvelx.util.Form
import com.alexgreece45.marvelx.util.FormValidationResult
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.Rule
import kotlinx.coroutines.launch
import org.koin.android.annotation.KoinViewModel

sealed interface EditProfileDetailsContract {
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
            data class OnGenderSelected(val value: Gender) : GenderField
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
        val snackbarStateData: SnackbarStateData? = null
    ) : BaseState

    sealed interface Effect : BaseEffect {
        sealed interface Navigation : Effect {
            data object NavigateUp : Navigation
        }
    }
}

@KoinViewModel
class EditProfileDetailsViewModel(
    private val userInteractor: UserInteractor,
    private val fieldValidationRules: FieldValidationRules,
    private val formValidator: FormValidator,
    private val resourceProvider: ResourceProvider
) : BaseViewModel<EditProfileDetailsContract.Event, EditProfileDetailsContract.State, EditProfileDetailsContract.Effect>() {
    override fun setInitialState(): EditProfileDetailsContract.State =
        EditProfileDetailsContract.State()

    override fun handleEvents(event: EditProfileDetailsContract.Event) {
        when (event) {
            EditProfileDetailsContract.Event.Init -> {
                loadProfileDetails(event)
            }

            is EditProfileDetailsContract.Event.FirstNameField.OnValueChanged -> {
                setState {
                    copy(
                        profileDetailsUi = profileDetailsUi?.copy(
                            firstName = profileDetailsUi.firstName.copy(
                                currentValue = event.value,
                                validationResult = validateField(
                                    value = event.value,
                                    validationRules = fieldValidationRules.getFirstNameValidationRules()
                                )
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

            is EditProfileDetailsContract.Event.LastNameField.OnValueChanged -> {
                setState {
                    copy(
                        profileDetailsUi = profileDetailsUi?.copy(
                            lastName = profileDetailsUi.lastName.copy(
                                currentValue = event.value,
                                validationResult = validateField(
                                    value = event.value,
                                    validationRules = fieldValidationRules.getLastNameValidationRules()
                                )
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

            is EditProfileDetailsContract.Event.GenderField.OnGenderSelected -> {
                setState {
                    copy(
                        profileDetailsUi = profileDetailsUi?.copy(
                            gender = profileDetailsUi.gender.copy(
                                currentValue = event.value,
                                validationResult = validateField(
                                    value = event.value.key,
                                    validationRules = fieldValidationRules.getGenderValidationRules()
                                )
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

            EditProfileDetailsContract.Event.GenderField.OnDismiss -> {
                setState {
                    copy(
                        isGenderDropDownExpanded = false
                    )
                }
            }

            EditProfileDetailsContract.Event.GenderField.OnClick -> {
                setState {
                    copy(
                        isGenderDropDownExpanded = !isGenderDropDownExpanded
                    )
                }
            }

            is EditProfileDetailsContract.Event.EmailField.OnValueChanged -> {
                setState {
                    copy(
                        profileDetailsUi = profileDetailsUi?.copy(
                            email = profileDetailsUi.email.copy(
                                currentValue = event.value,
                                validationResult = validateField(
                                    value = event.value,
                                    validationRules = fieldValidationRules.getEmailValidationRules()
                                )
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

            EditProfileDetailsContract.Event.OnBackClicked -> {
                if (currentState.isSaveChangesButtonEnabled) {
                    showAlertDialogForUnsavedChanges()
                } else {
                    setEffect {
                        EditProfileDetailsContract.Effect.Navigation.NavigateUp
                    }
                }
            }

            EditProfileDetailsContract.Event.OnSaveChangesButtonClicked -> {
                updateProfileDetails()
            }
        }
    }

    /**
     * Loads the profile details.
     */
    private fun loadProfileDetails(event: EditProfileDetailsContract.Event) {
        setState {
            copy(
                isLoading = true,
                errorView = null
            )
        }
        viewModelScope.launch {
            userInteractor.getCurrentUserBasicProfileDetails().collect {
                when (it) {
                    is GetCurrentUserProfileDetailsPartialState.Failed -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorView = EditProfileDetailsContract.ErrorView(
                                    errorTitle = resourceProvider.getString(R.string.edit_profile_details_screen_error_loading_profile_details),
                                    errorSubtitle = resourceProvider.getString(R.string.internal_server_error),
                                    onCloseClicked = EditProfileDetailsContract.Event.OnBackClicked,
                                    errorButtonLayout = ErrorButtonLayout.Single(
                                        primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                        onPrimaryButtonClicked = { loadProfileDetails(event) }
                                    )
                                )
                            )
                        }
                    }

                    is GetCurrentUserProfileDetailsPartialState.NoInternetConnection -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorView = EditProfileDetailsContract.ErrorView(
                                    errorTitle = resourceProvider.getString(R.string.edit_profile_details_screen_error_loading_profile_details),
                                    errorSubtitle = resourceProvider.getNoInternetConnectionMessage(),
                                    onCloseClicked = EditProfileDetailsContract.Event.OnBackClicked,
                                    errorButtonLayout = ErrorButtonLayout.Single(
                                        primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                        onPrimaryButtonClicked = { loadProfileDetails(event) }
                                    )
                                )
                            )
                        }
                    }

                    is GetCurrentUserProfileDetailsPartialState.Success -> {
                        setState {
                            copy(
                                isLoading = false,
                                errorView = null,
                                profileDetailsUi = it.basicProfileDetails.let { profileDetails ->
                                    ProfileDetailsUi(
                                        firstName = EditProfileDetailsItemUi(
                                            initialValue = profileDetails.firstName,
                                            currentValue = profileDetails.firstName,
                                            label = R.string.generic_label_first_name,
                                            onValueChanged = { value ->
                                                EditProfileDetailsContract.Event.FirstNameField.OnValueChanged(
                                                    value
                                                )
                                            },
                                            onResetIconClicked = EditProfileDetailsContract.Event.FirstNameField.OnValueChanged(
                                                value = profileDetails.firstName
                                            )
                                        ),
                                        lastName = EditProfileDetailsItemUi(
                                            initialValue = profileDetails.lastName,
                                            currentValue = profileDetails.lastName,
                                            label = R.string.generic_label_last_name,
                                            onValueChanged = { value ->
                                                EditProfileDetailsContract.Event.LastNameField.OnValueChanged(
                                                    value
                                                )
                                            },
                                            onResetIconClicked = EditProfileDetailsContract.Event.LastNameField.OnValueChanged(
                                                value = profileDetails.lastName
                                            )
                                        ),
                                        gender = EditProfileDetailsItemUi(
                                            initialValue = profileDetails.gender,
                                            currentValue = profileDetails.gender,
                                            label = R.string.generic_label_gender,
                                            onValueChanged = { value ->
                                                EditProfileDetailsContract.Event.GenderField.OnGenderSelected(
                                                    value
                                                )
                                            },
                                            onResetIconClicked = EditProfileDetailsContract.Event.GenderField.OnGenderSelected(
                                                value = profileDetails.gender
                                            )
                                        ),
                                        email = EditProfileDetailsItemUi(
                                            initialValue = profileDetails.email,
                                            currentValue = profileDetails.email,
                                            label = R.string.generic_label_email,
                                            keyboardType = KeyboardType.Email,
                                            onValueChanged = { value ->
                                                EditProfileDetailsContract.Event.EmailField.OnValueChanged(
                                                    value
                                                )
                                            },
                                            onResetIconClicked = EditProfileDetailsContract.Event.EmailField.OnValueChanged(
                                                value = profileDetails.email
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
                    errorView = EditProfileDetailsContract.ErrorView(
                        errorTitle = resourceProvider.getString(R.string.edit_profile_details_screen_error_updating_profile_details),
                        errorSubtitle = resourceProvider.getGenericErrorMessage(),
                        onCloseClicked = EditProfileDetailsContract.Event.OnBackClicked,
                        errorButtonLayout = ErrorButtonLayout.Single(
                            primaryButtonLabel = resourceProvider.getString(R.string.generic_ok_button),
                            onPrimaryButtonClicked = {
                                setEffect {
                                    EditProfileDetailsContract.Effect.Navigation.NavigateUp
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
        currentState.profileDetailsUi?.toBasicProfileDetailsDomain()?.let { profileDetailsDomain ->
            viewModelScope.launch {
                userInteractor.updateCurrentUserProfileDetails(profileDetailsDomain).collect {
                    when (it) {
                        is InteractorPartialState.Success -> {
                            // TODO: Review snackbar handling.
                            setState {
                                copy(
                                    snackbarStateData = SnackbarStateData(
                                        message = resourceProvider.getString(R.string.edit_profile_details_screen_success_updating_profile_details)
                                    )
                                )
                            }
                            setEffect {
                                EditProfileDetailsContract.Effect.Navigation.NavigateUp
                            }
                        }

                        is InteractorPartialState.Failed, is InteractorPartialState.Error -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    errorView = EditProfileDetailsContract.ErrorView(
                                        errorTitle = resourceProvider.getString(R.string.edit_profile_details_screen_error_updating_profile_details),
                                        errorSubtitle = resourceProvider.getString(R.string.internal_server_error),
                                        onCloseClicked = EditProfileDetailsContract.Event.OnBackClicked,
                                        errorButtonLayout = ErrorButtonLayout.Single(
                                            primaryButtonLabel = resourceProvider.getString(R.string.generic_retry_button),
                                            onPrimaryButtonClicked = ::updateProfileDetails
                                        )
                                    )
                                )
                            }
                        }

                        is InteractorPartialState.NoInternetConnection -> {
                            setState {
                                copy(
                                    isLoading = false,
                                    errorView = EditProfileDetailsContract.ErrorView(
                                        errorTitle = resourceProvider.getString(R.string.edit_profile_details_screen_error_updating_profile_details),
                                        errorSubtitle = resourceProvider.getNoInternetConnectionMessage(),
                                        onCloseClicked = EditProfileDetailsContract.Event.OnBackClicked,
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
    private fun validateField(
        value: String,
        validationRules: List<Rule>
    ): FormValidationResult {
        return formValidator.validateForm(Form(mapOf(validationRules to value)))
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
                alertDialogView = EditProfileDetailsContract.AlertDialogView(
                    title = resourceProvider.getString(R.string.edit_profile_details_screen_alert_dialog_unsaved_changes_title),
                    errorMessage = resourceProvider.getString(R.string.edit_profile_details_screen_alert_dialog_unsaved_changes_subtitle),
                    dismissButton = AlertDialogButton(
                        label = resourceProvider.getString(R.string.generic_no_button),
                        onClick = {
                            setState {
                                copy(
                                    alertDialogView = null
                                )
                            }
                        }
                    ),
                    confirmButton = AlertDialogButton(
                        label = resourceProvider.getString(R.string.generic_yes_button),
                        buttonType = ErrorButtonType.Contained,
                        onClick = {
                            setEffect {
                                EditProfileDetailsContract.Effect.Navigation.NavigateUp
                            }
                        }
                    )
                )
            )
        }
    }
}
