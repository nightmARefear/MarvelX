package com.alexgreece45.marvelx.data.models.otp

import com.alexgreece45.marvelx.data.domains.otp.OtpResponseErrorDomain
import com.google.gson.annotations.SerializedName

data class OtpErrorResponse(
    @SerializedName("error_code") val errorCode: String,
    @SerializedName("error_message") val errorMessage: String? = null,
    @SerializedName("otp_error_cause") val errorCause: String
)

fun OtpErrorResponse.toDomain(): OtpResponseErrorDomain =
    OtpResponseErrorDomain(
        errorCode = this.errorCode.toInt(),
        errorMessage = this.errorMessage,
        errorCause = OtpErrorCause.of(this.errorCause)
    )
