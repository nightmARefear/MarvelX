package com.alexgreece45.marvelx.data.models.token

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("access_token") val accessToken: String? = null,
    @SerializedName("expires_in") val accessExpiresIn: Int? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("refresh_expires_in") val refreshExpiresIn: Int? = null,
    @SerializedName("token_type") val tokenType: String? = null
)
