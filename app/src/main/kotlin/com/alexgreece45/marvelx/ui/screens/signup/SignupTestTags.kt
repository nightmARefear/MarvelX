package com.alexgreece45.marvelx.ui.screens.signup

internal object SignupTestTags {
    // SignupEvent.OnBack
    const val BACK_BUTTON = "signup_back_button"

    // SignupEvent.FieldValueChange.FirstName
    const val FIRST_NAME_TEXT_FIELD = "signup_first_name_text_field"

    // SignupEvent.FieldValueChange.LastName
    const val LAST_NAME_TEXT_FIELD = "signup_last_name_text_field"

    // SignupEvent.GenderDropdownMenu.OnClick / OnDismiss
    const val GENDER_DROPDOWN = "signup_gender_dropdown"

    // SignupEvent.FieldValueChange.Email
    const val EMAIL_TEXT_FIELD = "signup_email_text_field"

    // SignupEvent.FieldValueChange.Password
    const val PASSWORD_TEXT_FIELD = "signup_password_text_field"

    // SignupEvent.OnPasswordVisibilityIconClick
    const val PASSWORD_VISIBILITY_BUTTON = "signup_password_visibility_button"

    // SignupEvent.OnSignupButtonClick
    const val SUBMIT_BUTTON = "signup_submit_button"

    /**
     * SignupEvent.FieldValueChange.GenderDropDown; one tag per option, keyed by `Gender.key`
     * (e.g. `signup_gender_female_option`).
     */
    fun genderOption(genderKey: String) = "signup_gender_${genderKey}_option"
}
