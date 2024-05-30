package com.alexgreece45.marvelx.network_logic.models_api.user.signup

import com.google.gson.annotations.SerializedName

data class ResponseSignup(
    @SerializedName("verificationStep") val verificationStep: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("authCode") val authCode: String
)
