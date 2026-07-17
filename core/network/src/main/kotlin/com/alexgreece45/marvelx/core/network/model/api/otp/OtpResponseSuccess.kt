package com.alexgreece45.marvelx.core.network.model.api.otp

import com.google.gson.annotations.SerializedName

data class OtpResponseSuccess(
    @SerializedName("temp_auth_code") val tempAuthCode: String
)
