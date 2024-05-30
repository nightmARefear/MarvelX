package com.alexgreece45.marvelx.network_logic.models_api.forgotPassword.phoneNumber

import com.google.gson.annotations.SerializedName

/**
 * Represents a request for the "forgot password" flow, by requesting an OTP to be sent to the given
 * phone number ([phoneNumber]) of the user.
 *
 * @param phoneNumber   The phone number of the user that the OTP will be sent to.
 */
data class RequestForgotPasswordSendOtpToPhoneNumber(
    @SerializedName("phoneNumber") val phoneNumber: String
)