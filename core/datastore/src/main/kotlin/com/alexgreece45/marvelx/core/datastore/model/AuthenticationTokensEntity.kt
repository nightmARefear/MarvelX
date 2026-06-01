package com.alexgreece45.marvelx.core.datastore.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class AuthenticationTokensEntity(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("accessExpiresIn") val accessExpiresIn: Long,
    @SerialName("refreshToken") val refreshToken: String,
    @SerialName("refreshExpiresIn") val refreshExpiresIn: Long,
    @SerialName("tokenType") val tokenType: String
)