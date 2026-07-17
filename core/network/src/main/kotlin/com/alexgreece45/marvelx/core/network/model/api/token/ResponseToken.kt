package com.alexgreece45.marvelx.core.network.model.api.token

import com.alexgreece45.marvelx.model.model.AuthenticationTokens
import com.google.gson.annotations.SerializedName

data class ResponseToken(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("expires_in") val accessExpiresIn: Int,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("refresh_expires_in") val refreshExpiresIn: Int,
    @SerializedName("token_type") val tokenType: String
)

fun ResponseToken.toModel(): AuthenticationTokens =
    AuthenticationTokens(
        accessToken = this.accessToken,
        accessExpiresIn = this.accessExpiresIn.toLong(),
        refreshToken = this.refreshToken,
        refreshExpiresIn = this.refreshExpiresIn.toLong(),
        tokenType = this.tokenType
    )