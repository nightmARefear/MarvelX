package com.alexgreece45.marvelx.data.models.otp

sealed class OtpErrorCause(val key: String, val cause: String) {
    data object NotMatch : OtpErrorCause(key = "otp_not_match", cause = "OTP is incorrect.")

    data object InvalidLength :
        OtpErrorCause("otp_invalid_length", cause = "OTP must be 6 characters.")

    data object Expired :
        OtpErrorCause(key = "otp_expired", cause = "The OTP is expired. Please try again.")

    data object InternalServerError :
        OtpErrorCause(key = "internal_server_error", cause = "Internal server error.")

    data class Invalid(val value: String) :
        OtpErrorCause(key = value, cause = "Invalid OTP Error cause type \"$value\".")

    companion object {
        private fun asList(): List<OtpErrorCause> =
            listOf(NotMatch, InvalidLength, Expired, InternalServerError)

        fun of(value: String): OtpErrorCause =
            asList().firstOrNull { it.key == value } ?: Invalid(value = value)
    }
}
