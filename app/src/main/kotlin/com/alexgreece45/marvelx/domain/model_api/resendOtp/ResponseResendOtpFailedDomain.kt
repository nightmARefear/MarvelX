package com.alexgreece45.marvelx.domain.model_api.resendOtp

import com.alexgreece45.marvelx.core.network.model.api.resendOtp.ResponseResendOtpFailed

data class ResponseResendOtpFailedDomain(
    val resendOtpErrorCause: ResendOtpErrorCause,
    val expiresIn: Long?
)

fun ResponseResendOtpFailed.toResponseResendOtpFailedDomain(): ResponseResendOtpFailedDomain =
    ResponseResendOtpFailedDomain(
        resendOtpErrorCause = ResendOtpErrorCause.of(this.resendOtpErrorCause),
        expiresIn = this.expiresIn
    )