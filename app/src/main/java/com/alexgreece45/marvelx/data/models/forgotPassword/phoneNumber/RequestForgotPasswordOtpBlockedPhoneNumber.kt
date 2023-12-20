package com.alexgreece45.marvelx.data.models.forgotPassword.phoneNumber

import com.google.gson.annotations.SerializedName

/**
 * Represents the request to check whether the "forgot OTP" functionality for the phone number is
 * blocked.
 *
 * @param requestId     The unique ID for the request. Retrieved earlier.
 */
data class RequestForgotPasswordOtpBlockedPhoneNumber(
    @SerializedName("requestId") val requestId: String
)
