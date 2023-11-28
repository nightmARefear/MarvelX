package com.alexgreece45.marvelx.data.models.resendOtp

import com.alexgreece45.marvelx.data.domains.resendOtp.ResendOtpErrorCause
import com.alexgreece45.marvelx.data.domains.resendOtp.ResponseResendOtpFailedDomain
import com.google.gson.annotations.SerializedName

data class ResponseResendOtpFailed(
    @SerializedName("resend_otp_error_cause") val resendOtpErrorCause: String,
    @SerializedName("expiresIn") val expiresIn: Long?
)

fun ResponseResendOtpFailed.toResponseResendOtpFailedDomain(): ResponseResendOtpFailedDomain =
    ResponseResendOtpFailedDomain(
        resendOtpErrorCause = ResendOtpErrorCause.of(this.resendOtpErrorCause),
        expiresIn = this.expiresIn
    )