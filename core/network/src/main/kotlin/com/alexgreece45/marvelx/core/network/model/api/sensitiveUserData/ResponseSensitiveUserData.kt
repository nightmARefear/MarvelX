package com.alexgreece45.marvelx.core.network.model.api.sensitiveUserData

import com.google.gson.annotations.SerializedName

/**
 * Defines the response of [RequestSensitiveUserData].
 *
 * Only used for displaying masked emails / phone numbers
 * to the screen for requesting new OTPs.
 *
 * @param type                  Refers to the type of the retrieved sensitive
 * user data (i.e., email, or phone_number).
 * @param data                  Refers to the value of the retrieved sensitive
 * user data (i.e., the stored email address, or phone number).
 * @param otpInterval           The amount of seconds until a new OTP can be requested.
 * @param isOtpBlocked          Defines whether the `OTP` functionality is blocked.
 * @param isResendOtpBlocked    Defines whether the `Resend OTP` functionality is blocked.
 * @param otpBlockExpiresIn     Defines the remaining amount of time in seconds, from now, until the
 * `OTP` functionality is enabled so that new OTPs can be requested.
 */
data class ResponseSensitiveUserData(
    @SerializedName("type") val type: String,
    @SerializedName("data") val data: String,
    @SerializedName("otpInterval") val otpInterval: Long,
    @SerializedName("isOtpBlocked") val isOtpBlocked: Boolean,
    @SerializedName("isResendOtpBlocked") val isResendOtpBlocked: Boolean,
    @SerializedName("otpBlockExpiresIn") val otpBlockExpiresIn: Long?
)