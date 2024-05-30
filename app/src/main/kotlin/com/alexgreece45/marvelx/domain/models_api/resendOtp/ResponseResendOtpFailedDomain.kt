package com.alexgreece45.marvelx.domain.models_api.resendOtp

data class ResponseResendOtpFailedDomain(
    val resendOtpErrorCause: ResendOtpErrorCause,
    val expiresIn: Long?
)
