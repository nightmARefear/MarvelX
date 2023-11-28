package com.alexgreece45.marvelx.data.domains.resendOtp

data class ResponseResendOtpFailedDomain(
    val resendOtpErrorCause: ResendOtpErrorCause,
    val expiresIn: Long?
)
