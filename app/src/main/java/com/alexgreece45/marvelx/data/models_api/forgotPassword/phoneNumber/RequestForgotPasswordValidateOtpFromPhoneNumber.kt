package com.alexgreece45.marvelx.data.models_api.forgotPassword.phoneNumber

import com.google.gson.annotations.SerializedName

/**
 * This data class represents a request to validate the given [otp] against the one that was sent to
 * the phone number of the user.
 *
 * @param requestId     The unique ID for the request. Retrieved earlier.
 * @param otp           The OTP that the user has entered.
 */
data class RequestForgotPasswordValidateOtpFromPhoneNumber(
    @SerializedName("requestId") val requestId: String,
    @SerializedName("otp") val otp: String
)