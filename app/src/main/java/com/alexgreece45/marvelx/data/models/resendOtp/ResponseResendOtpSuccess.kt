package com.alexgreece45.marvelx.data.models.resendOtp

import com.google.gson.annotations.SerializedName

data class ResponseResendOtpSuccess(
    @SerializedName("expiresIn") val expiresIn: Long
)