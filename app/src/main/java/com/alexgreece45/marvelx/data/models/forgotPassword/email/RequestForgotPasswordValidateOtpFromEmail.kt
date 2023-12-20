package com.alexgreece45.marvelx.data.models.forgotPassword.email

import com.google.gson.annotations.SerializedName

/**
 * This data class represents a request to validate the given [otp] against the one that was sent to
 * the email of the user.
 *
 * @param requestId     The unique ID for the request. Retrieved earlier.
 * @param otp           The OTP that the user has entered.
 */
data class RequestForgotPasswordValidateOtpFromEmail(
    @SerializedName("requestId") val requestId: String,
    @SerializedName("otp") val otp: String
)