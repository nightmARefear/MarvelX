package com.alexgreece45.marvelx.data.models.otp

import com.alexgreece45.marvelx.data.domains.otp.OtpResponseErrorDomain
import com.google.gson.annotations.SerializedName

/**
 * Defines the error response of an OTP request.
 *
 * @param errorCause    The OTP error cause (i.e., OTP is incorrect, OTP is of invalid length, OTP is expired).
 * */
data class OtpResponseError(
    @SerializedName("otp_error_cause") val errorCause: String
)

fun OtpResponseError.otpResponseErrorDomain(): OtpResponseErrorDomain =
    OtpResponseErrorDomain(
        errorCause = OtpErrorCause.of(this.errorCause)
    )
