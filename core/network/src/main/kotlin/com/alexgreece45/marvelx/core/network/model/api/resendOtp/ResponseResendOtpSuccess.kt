package com.alexgreece45.marvelx.core.network.model.api.resendOtp

import com.google.gson.annotations.SerializedName

data class ResponseResendOtpSuccess(
    @SerializedName("expiresIn") val expiresIn: Long
)