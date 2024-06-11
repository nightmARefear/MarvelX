package com.alexgreece45.marvelx.ui.screens.profile.model

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.network_logic.models_api.user.Gender
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsContract
import com.alexgreece45.marvelx.util.FormValidationResult

/**
 * Defines all the UI elements for the profile details section.
 */
data class ProfileDetailsUi(
    val firstName: ProfileDetailsItemUi<String>,
    val lastName: ProfileDetailsItemUi<String>,
    val gender: ProfileDetailsItemUi<Gender>,
    val email: ProfileDetailsItemUi<String>
)

fun ProfileDetailsUi.toProfileDetailsDomain(): ProfileDetailsDomain =
    ProfileDetailsDomain(
        firstName = firstName.currentValue,
        lastName = lastName.currentValue,
        fullName = "${firstName.currentValue} ${lastName.currentValue}",
        gender = gender.currentValue,
        email = email.currentValue
    )

/**
 * Defines a single UI item for the profile details screen.
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
data class ProfileDetailsItemUi<T>(
    val initialValue: T,
    val currentValue: T,
    @StringRes val label: Int,
    val onValueChanged: (T) -> ProfileDetailsContract.Event,
    val onResetIconClicked: ProfileDetailsContract.Event,
    val validationResult: FormValidationResult = FormValidationResult(isValid = true)
)
