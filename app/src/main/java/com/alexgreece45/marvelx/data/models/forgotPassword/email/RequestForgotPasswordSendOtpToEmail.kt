package com.alexgreece45.marvelx.data.models.forgotPassword.email

import com.google.gson.annotations.SerializedName

/**
 * Represents a request for the "forgot password" flow, by requesting an OTP to be sent to the given
 * email ([email]) of the user.
 *
 * @param email The email of the user that the OTP will be sent to.
 */
data class RequestForgotPasswordSendOtpToEmail(
    @SerializedName("email") val email: String
)