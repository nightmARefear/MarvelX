package com.alexgreece45.marvelx.domain.model_api.jwtSensitiveUserData

import com.alexgreece45.marvelx.core.network.model.api.jwtSensitiveUserData.ResponseJwtSensitiveUserData

data class JwtSensitiveUserDataDomain(
    val token: String
)

fun ResponseJwtSensitiveUserData.toJwtSensitiveUserDataDomain(): JwtSensitiveUserDataDomain =
    JwtSensitiveUserDataDomain(
        token = this.token
    )