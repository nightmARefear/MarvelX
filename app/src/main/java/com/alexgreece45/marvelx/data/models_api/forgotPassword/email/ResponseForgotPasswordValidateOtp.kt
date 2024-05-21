package com.alexgreece45.marvelx.data.models_api.forgotPassword.email

import com.google.gson.annotations.SerializedName

/**
 * This data class represents the successful response of [RequestForgotPasswordSendOtp].
 *
 * - If [isPhoneNumberRequired] is `true` that means that the user has set a 3FA for resetting their
 * password, and that an OTP must be sent to their phone number for proceeding further in the `forgot
 * password` flow.
 *
 * - If [isPhoneNumberRequired] is `false` that means that no other OTPs have to be sent, and the
 * user can enter their new password.
 */
data class ResponseForgotPasswordValidateOtp(
    @SerializedName("isPhoneNumberRequired") val isPhoneNumberRequired: Boolean
)