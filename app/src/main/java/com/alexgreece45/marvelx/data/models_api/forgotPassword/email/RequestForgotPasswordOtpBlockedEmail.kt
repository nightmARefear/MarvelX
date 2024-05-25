package com.alexgreece45.marvelx.data.models_api.forgotPassword.email

import com.google.gson.annotations.SerializedName

/**
 * Represents the request to check whether the "forgot OTP" functionality for the email is blocked.
 *
 * @param requestId     The unique ID for the request. Retrieved earlier.
 */
data class RequestForgotPasswordOtpBlockedEmail(
    @SerializedName("requestId") val requestId: String
)