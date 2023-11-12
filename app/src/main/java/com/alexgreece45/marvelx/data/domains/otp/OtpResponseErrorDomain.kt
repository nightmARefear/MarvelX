package com.alexgreece45.marvelx.data.domains.otp

import com.alexgreece45.marvelx.data.models.otp.OtpErrorCause

/**
 * Defines the error response of an OTP request.
 *
 * @param errorCause    The OTP error cause (i.e., OTP is incorrect, OTP is of invalid length, OTP is expired, internal server error).
 * */
data class OtpResponseErrorDomain(
    val errorCause: OtpErrorCause
)
