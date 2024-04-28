package com.alexgreece45.marvelx.data.models.forgotPassword.email

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.google.gson.annotations.SerializedName

/**
 * Represents a request for the `forgot password` flow, by requesting an OTP to be sent to the given
 * data (i.e., `email` or `phone number`) of the user based on the given [verificationStep].
 *
 * @param data The email of the user that the OTP will be sent to.
 */
data class RequestForgotPasswordSendOtp(
    @SerializedName("data") val data: String,
    @SerializedName("verificationStep") val verificationStep: String
)