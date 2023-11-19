package com.alexgreece45.marvelx.data.models.jwtSensitiveUserData

import com.alexgreece45.marvelx.data.domains.jwtSensitiveUserData.JwtSensitiveUserDataDomain
import com.google.gson.annotations.SerializedName

data class ResponseJwtSensitiveUserData(
    @SerializedName("token") val token: String
)

fun ResponseJwtSensitiveUserData.toJwtSensitiveUserDataDomain(): JwtSensitiveUserDataDomain =
    JwtSensitiveUserDataDomain(
        token = this.token
    )