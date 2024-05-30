package com.alexgreece45.marvelx.network_logic.models_api.otp

import com.alexgreece45.marvelx.domain.models_api.otp.OtpDomain
import com.google.gson.annotations.SerializedName

data class OtpResponseSuccess(
    @SerializedName("authentication_code") val authenticationCode: String
)

fun OtpResponseSuccess.toOtpDomain(): OtpDomain =
    OtpDomain(
        authenticationCode = this.authenticationCode
    )
