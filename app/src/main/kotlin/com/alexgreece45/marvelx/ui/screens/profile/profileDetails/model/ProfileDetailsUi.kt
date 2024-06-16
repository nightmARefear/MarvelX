package com.alexgreece45.marvelx.ui.screens.profile.profileDetails.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.PrivacySettingsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.UserStatsDomain
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

fun ProfileDetailsUi.toProfileDetailsDomain(): ProfileDetailsDomain =
    ProfileDetailsDomain(
        firstName = firstName.currentValue,
        lastName = lastName.currentValue,
        gender = gender.currentValue,
        email = email.currentValue,
        profileImageUrl = "",
        fullName = "${firstName.currentValue} ${lastName.currentValue}",
        isDifferentUser = false,// This value doesn't matter for this transformation.
        privacySettings = PrivacySettingsDomain(
            isFriendsListVisible = false,
            isListingsListVisible = false,
            isFavoritesListVisible = false,
            isGenderVisible = false,
            isEmailVisible = false
        ), // These values don't matter for this transformation.
        userStats = UserStatsDomain("", "", "")// These values don't matter for this transformation.
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
