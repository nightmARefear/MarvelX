package com.alexgreece45.marvelx.domain.models_api.resendOtp

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R

sealed class ResendOtpErrorCause(val key: String, @StringRes val cause: Int) {
    /**
     * The user couldn't be found.
     */
    data object UserNotFound :
        ResendOtpErrorCause(
            key = "user_not_found",
            cause = R.string.resend_otp_error_cause_user_not_found
        )

    /**
     * Defines that the `OTP` functionality has been blocked. That means that the user has
     * incorrectly entered OTPs many times.
     */
    data object Blocked :
        ResendOtpErrorCause(
            key = "otp_blocked",
            cause = R.string.otp_error_cause_blocked
        )

    /**
     * Defines that the user has requested too soon for an OTP resend.
     */
    data object TooSoon :
        ResendOtpErrorCause(
            key = "resend_otp_too_soon",
            cause = R.string.resend_otp_error_cause_too_soon
        )

    /**
     * Represents an internal server error.
     */
    data object InternalServerError :
        ResendOtpErrorCause(
            key = "internal_server_error",
            cause = R.string.internal_server_error
        )

    /**
     * Represents an invalid `ResendOtpErrorCause` type.
     */
    data class Invalid(val value: String) :
        ResendOtpErrorCause(key = value, cause = R.string.otp_error_cause_invalid)

    companion object {
        private fun asList(): List<ResendOtpErrorCause> =
            listOf(UserNotFound, Blocked, TooSoon, InternalServerError)

        fun of(value: String): ResendOtpErrorCause =
            asList().firstOrNull { it.key == value } ?: Invalid(value = value)
    }
}