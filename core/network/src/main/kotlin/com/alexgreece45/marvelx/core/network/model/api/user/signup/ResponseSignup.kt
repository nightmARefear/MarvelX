package com.alexgreece45.marvelx.core.network.model.api.user.signup

import com.google.gson.annotations.SerializedName

data class ResponseSignup(
    @SerializedName("verificationStep") val verificationStep: String,
    @SerializedName("userId") val userId: String,
    @SerializedName("tempAuthCode") val tempAuthCode: String
)
