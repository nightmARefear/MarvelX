package com.alexgreece45.marvelx.network_logic.models_api.resendOtp

import com.google.gson.annotations.SerializedName

data class ResponseResendOtpSuccess(
    @SerializedName("expiresIn") val expiresIn: Long
)