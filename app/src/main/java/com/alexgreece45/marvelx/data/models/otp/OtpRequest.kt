package com.alexgreece45.marvelx.data.models.otp

import com.google.gson.annotations.SerializedName

data class OtpRequest(
    @SerializedName("user_id") val userId: String,
    @SerializedName("otp") val otp: String
)