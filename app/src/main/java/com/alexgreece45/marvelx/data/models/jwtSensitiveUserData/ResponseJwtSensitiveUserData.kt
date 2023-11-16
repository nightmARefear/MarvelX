package com.alexgreece45.marvelx.data.models.jwtSensitiveUserData

import com.alexgreece45.marvelx.data.domains.jwtSensitiveUserData.JwtSensitiveUserDataDomain
import com.google.gson.annotations.SerializedName

data class ResponseJwtSensitiveUserData(
    @SerializedName("jwt") val jwt: String
)

fun ResponseJwtSensitiveUserData.toJwtSensitiveUserDataDomain(): JwtSensitiveUserDataDomain =
    JwtSensitiveUserDataDomain(
        jwt = this.jwt
    )