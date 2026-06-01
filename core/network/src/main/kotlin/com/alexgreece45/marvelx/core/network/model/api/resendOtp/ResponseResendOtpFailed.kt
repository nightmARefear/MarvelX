package com.alexgreece45.marvelx.core.network.model.api.resendOtp

import com.google.gson.annotations.SerializedName

data class ResponseResendOtpFailed(
    @SerializedName("resend_otp_error_cause") val resendOtpErrorCause: String,
    @SerializedName("expiresIn") val expiresIn: Long?
)