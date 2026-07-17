package com.alexgreece45.marvelx.core.network.model.api.jwtSensitiveUserData

import com.google.gson.annotations.SerializedName

data class ResponseJwtSensitiveUserData(
    @SerializedName("token") val token: String
)