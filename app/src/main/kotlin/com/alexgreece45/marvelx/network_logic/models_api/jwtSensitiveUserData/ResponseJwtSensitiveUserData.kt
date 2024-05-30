package com.alexgreece45.marvelx.network_logic.models_api.jwtSensitiveUserData

import com.alexgreece45.marvelx.domain.models_api.jwtSensitiveUserData.JwtSensitiveUserDataDomain
import com.google.gson.annotations.SerializedName

data class ResponseJwtSensitiveUserData(
    @SerializedName("token") val token: String
)

fun ResponseJwtSensitiveUserData.toJwtSensitiveUserDataDomain(): JwtSensitiveUserDataDomain =
    JwtSensitiveUserDataDomain(
        token = this.token
    )