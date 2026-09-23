package com.alexgreece45.marvelx.ui.screens.login

internal object LoginTestTags {
    // LoginEvent.OnBack
    const val BACK_BUTTON = "login_back_button"

    // LoginEvent.FieldValueChange.Email
    const val EMAIL_TEXT_FIELD = "login_email_text_field"

    // LoginEvent.FieldValueChange.Password
    const val PASSWORD_TEXT_FIELD = "login_password_text_field"

    // LoginEvent.OnPasswordVisibilityIconClick
    const val PASSWORD_VISIBILITY_BUTTON = "login_password_visibility_button"

    // LoginEvent.OnRememberMeCheckboxClick
    const val REMEMBER_ME_CHECKBOX = "login_remember_me_checkbox"

    // LoginEvent.OnForgotPasswordButtonClick
    const val FORGOT_PASSWORD_BUTTON = "login_forgot_password_button"

    // LoginEvent.OnLoginButtonClick
    const val SUBMIT_BUTTON = "login_submit_button"

    // LoginEvent.OnLoginWithGoogleResult (fired after the Google sign-in flow returns)
    const val GOOGLE_SIGN_IN_BUTTON = "login_google_sign_in_button"

    // LoginEvent.OnSignupTextClicked
    const val SIGNUP_TEXT = "login_signup_text"
}
