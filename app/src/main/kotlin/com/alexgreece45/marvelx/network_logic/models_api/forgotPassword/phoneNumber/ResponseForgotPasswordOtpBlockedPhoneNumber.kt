package com.alexgreece45.marvelx.network_logic.models_api.forgotPassword.phoneNumber

import com.google.gson.annotations.SerializedName

/**
 * Defines the response of [RequestForgotPasswordOtpBlockedPhoneNumber].
 *
 * @param otpInterval           The amount of seconds until a new OTP can be requested.
 * @param isOtpBlocked          Whether the OTP functionality is blocked.
 * @param otpBlockExpiresIn     Defines the remaining amount of time in seconds, from now, until the
 * `OTP` functionality is enabled so that new OTPs can be requested / validated.
 */
data class ResponseForgotPasswordOtpBlockedPhoneNumber(
    @SerializedName("otpInterval") val otpInterval: Long,
    @SerializedName("isOtpBlocked") val isOtpBlocked: Boolean,
    @SerializedName("otpBlockExpiresIn") val otpBlockExpiresIn: Long
)