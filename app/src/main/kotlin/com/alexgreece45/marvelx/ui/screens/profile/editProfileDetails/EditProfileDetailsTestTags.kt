package com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails

internal object EditProfileDetailsTestTags {
    // EditProfileDetailsContract.Event.OnBackClicked
    const val BACK_BUTTON = "edit_profile_details_back_button"

    // EditProfileDetailsContract.Event.OnSaveChangesButtonClicked
    const val SAVE_CHANGES_BUTTON = "edit_profile_details_save_changes_button"

    // First name field value change (`EditProfileDetailsItemUi.onValueChanged`)
    const val FIRST_NAME_TEXT_FIELD = "edit_profile_details_first_name_text_field"

    // First name field reset (`EditProfileDetailsItemUi.onResetIconClicked`)
    const val FIRST_NAME_RESET_BUTTON = "edit_profile_details_first_name_reset_button"

    // Last name field value change
    const val LAST_NAME_TEXT_FIELD = "edit_profile_details_last_name_text_field"

    // Last name field reset
    const val LAST_NAME_RESET_BUTTON = "edit_profile_details_last_name_reset_button"

    // Email field value change
    const val EMAIL_TEXT_FIELD = "edit_profile_details_email_text_field"

    // Email field reset
    const val EMAIL_RESET_BUTTON = "edit_profile_details_email_reset_button"

    // EditProfileDetailsContract.Event.GenderField.OnClick / OnDismiss
    const val GENDER_DROPDOWN = "edit_profile_details_gender_dropdown"

    // The dialog buttons use the shared alert dialog tags (`alert_dialog_confirm_button`,
    // `alert_dialog_dismiss_button`); the error screen uses the shared content error tags.

    /**
     * EditProfileDetailsContract.Event.GenderField.OnGenderSelected; one tag per option, keyed by
     * `Gender.key` (e.g. `edit_profile_details_gender_female_option`).
     */
    fun genderOption(genderKey: String) = "edit_profile_details_gender_${genderKey}_option"
}
