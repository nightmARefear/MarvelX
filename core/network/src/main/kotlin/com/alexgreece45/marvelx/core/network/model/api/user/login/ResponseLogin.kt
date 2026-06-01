package com.alexgreece45.marvelx.core.network.model.api.user.login

import com.google.gson.annotations.SerializedName

data class ResponseLogin(
    @SerializedName("verificationStep") val verificationStep: String,
    @SerializedName("userId") val userId: String
)