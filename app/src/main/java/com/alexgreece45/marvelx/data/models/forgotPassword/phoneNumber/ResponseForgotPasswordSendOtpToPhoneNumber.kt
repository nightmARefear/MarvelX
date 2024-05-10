package com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber

import com.google.gson.annotations.SerializedName

/**
 * This data class represents the successful response of [RequestForgotPasswordValidateOtpFromPhoneNumber],
 * so that we can later on validate the user-entered OTP against the one that was sent to their
 * phone number.
 *
 * @param requestId The unique ID of the request. Will be used later for sending the OTPs.
 */
data class ResponseForgotPasswordSendOtpToPhoneNumber(
    @SerializedName("requestId") val requestId: String
)