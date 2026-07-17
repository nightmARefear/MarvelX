package com.alexgreece45.marvelx.domain.model_api.otp

import com.alexgreece45.marvelx.core.network.model.api.otp.OtpResponseError

/**
 * Defines the error response of an OTP request.
 *
 * @param errorCause        The OTP error cause (i.e., OTP is incorrect, OTP is of invalid length,
 * OTP is expired, internal server error, etc.).
 * @param blockExpiresIn    The number of seconds, from this time, until the client can request for
 * a new OTP. If not nullable, it must be shown next to the respective error, and the `verify OTP`
 * functionality must be disabled.
 */
data class OtpResponseErrorDomain(
    val errorCause: OtpErrorCause,
    val blockExpiresIn: Long?
)

fun OtpResponseError.toOtpResponseErrorDomain(): OtpResponseErrorDomain =
    OtpResponseErrorDomain(
        errorCause = OtpErrorCause.of(this.errorCause),
        blockExpiresIn = this.blockExpiresIn
    )