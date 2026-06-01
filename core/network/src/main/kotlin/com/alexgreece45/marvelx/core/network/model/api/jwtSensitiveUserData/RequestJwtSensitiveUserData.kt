package com.alexgreece45.marvelx.core.network.model.api.jwtSensitiveUserData

import com.google.gson.annotations.SerializedName

data class RequestJwtSensitiveUserData(
    @SerializedName("user_id") val userId: String,
    @SerializedName("verification_step") val verificationStep: String
)