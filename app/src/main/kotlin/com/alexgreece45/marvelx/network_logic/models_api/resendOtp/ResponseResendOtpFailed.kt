package com.alexgreece45.marvelx.network_logic.models_api.resendOtp

import com.alexgreece45.marvelx.domain.models_api.resendOtp.ResendOtpErrorCause
import com.alexgreece45.marvelx.domain.models_api.resendOtp.ResponseResendOtpFailedDomain
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