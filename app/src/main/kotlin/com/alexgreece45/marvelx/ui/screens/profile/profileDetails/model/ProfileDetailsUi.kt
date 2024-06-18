package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.BasicProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails.EditProfileDetailsContract
import com.alexgreece45.marvelx.util.FormValidationResult

/**
 * Defines all the UI elements for the profile details section.
 */
data class ProfileDetailsUi(
    val firstName: EditProfileDetailsItemUi<String>,
    val lastName: EditProfileDetailsItemUi<String>,
    val gender: EditProfileDetailsItemUi<Gender>,
    val email: EditProfileDetailsItemUi<String>
)

fun ProfileDetailsUi.toBasicProfileDetailsDomain(): BasicProfileDetailsDomain =
    BasicProfileDetailsDomain(
        firstName = this.firstName.currentValue,
        lastName = this.lastName.currentValue,
        gender = this.gender.currentValue,
        email = this.email.currentValue,
        profileImageUrl = "", // Doesn't matter for this transformation.
        fullName = "" // Doesn't matter for this transformation.
    )

/**
 * Defines a single UI item for the edit profile details screen.
 *
 * @param initialValue          The initial value of this item. Used to perform operations to change
 * the state of the `save changes` button.
 * @param currentValue          The current value of this item.
 * @param label                 The label of this item.
 * @param onValueChanged        The action(s) to invoke when the value of this item changes.
 * @param onResetIconClicked    The action(s) to invoke when the reset icon of this item is clicked.
 * @param validationResult      The validation result of this item. It has the initial value of
 * `isValid = true`.
 */
data class EditProfileDetailsItemUi<T>(
    val initialValue: T,
    val currentValue: T,
    @StringRes val label: Int,
    val onValueChanged: (T) -> EditProfileDetailsContract.Event,
    val onResetIconClicked: EditProfileDetailsContract.Event,
    val validationResult: FormValidationResult = FormValidationResult(isValid = true)
)
