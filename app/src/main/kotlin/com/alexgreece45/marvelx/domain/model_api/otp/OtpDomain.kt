package com.alexgreece45.marvelx.domain.model_api.otp

import com.alexgreece45.marvelx.core.network.model.api.otp.OtpResponseSuccess

data class OtpDomain(
    val tempAuthCode: String
)

fun OtpResponseSuccess.toOtpDomain(): OtpDomain =
    OtpDomain(
        tempAuthCode = this.tempAuthCode
    )
