package com.alexgreece45.marvelx.domain.model_api.otp

import com.alexgreece45.marvelx.network_logic.models_api.otp.OtpResponseSuccess

data class OtpDomain(
    val tempAuthCode: String
)

fun OtpResponseSuccess.toOtpDomain(): OtpDomain =
    OtpDomain(
        tempAuthCode = this.tempAuthCode
    )
