package com.alexgreece45.marvelx.data.models.otp

import com.google.gson.annotations.SerializedName

data class OtpResponseSuccess(
    @SerializedName("access_token") val accessToken: String?
)
