package com.alexgreece45.marvelx.data.domains.otp

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R

sealed class OtpErrorCause(val key: String, @StringRes val cause: Int) {
    data object NotMatch :
        OtpErrorCause(key = "otp_not_match", cause = R.string.otp_error_cause_not_match)

    data object InvalidLength :
        OtpErrorCause(
            "otp_invalid_length",
            cause = R.string.otp_error_cause_invalid_length
        )

    data object Expired :
        OtpErrorCause(key = "otp_expired", cause = R.string.otp_error_cause_expired)

    data object Blocked :
        OtpErrorCause(
            key = "otp_blocked",
            cause = R.string.otp_error_cause_blocked
        )

    data object NotFound :
        OtpErrorCause(
            key = "otp_not_found",
            cause = R.string.internal_server_error
        )

    data object InternalServerError :
        OtpErrorCause(
            key = "internal_server_error",
            cause = R.string.internal_server_error
        )

    data class Invalid(val value: String) :
        OtpErrorCause(key = value, cause = R.string.otp_error_cause_invalid)

    companion object {
        private fun asList(): List<OtpErrorCause> =
            listOf(NotMatch, InvalidLength, Expired, Blocked, NotFound, InternalServerError)

        fun of(value: String): OtpErrorCause =
            asList().firstOrNull { it.key == value } ?: Invalid(value = value)
    }
}