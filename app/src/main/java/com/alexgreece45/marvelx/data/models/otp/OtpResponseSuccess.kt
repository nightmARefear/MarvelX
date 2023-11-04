package com.alexgreece45.marvelx.data.models.otp

import com.alexgreece45.marvelx.data.domains.otp.OtpDomain
import com.google.gson.annotations.SerializedName

data class OtpResponseSuccess(
    @SerializedName("code") val code: String
)

fun OtpResponseSuccess.toOtpDomain(): OtpDomain =
    OtpDomain(
        code = this.code
    )
