package com.alexgreece45.marvelx.core.network.model.api.forgotPassword.email

import com.google.gson.annotations.SerializedName

/**
 * This data class represents a request to validate the given [otp] against the one that was sent to
 * the email/phone number of the user.
 *
 * @param requestId         The unique ID for the request. Retrieved earlier.
 * @param otp               The OTP that the user has entered.
 * @param verificationStep  The verification step as a [String] describing if the step is validation
 * via email or phone number.
 */
data class RequestForgotPasswordValidateOtp(
    @SerializedName("requestId") val requestId: String,
    @SerializedName("otp") val otp: String,
    @SerializedName("verificationStep") val verificationStep: String
)