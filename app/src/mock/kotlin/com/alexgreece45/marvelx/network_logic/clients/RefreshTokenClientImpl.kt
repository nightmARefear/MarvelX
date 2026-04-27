package com.alexgreece45.marvelx.network_logic.clients

import com.alexgreece45.marvelx.network_logic.models_api.token.ResponseToken
import retrofit2.Response

class RefreshTokenClientImpl : RefreshTokenClient {

    override suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Response<ResponseToken> = Response.success(
        200, ResponseToken(
            accessToken = "ACCESS_TOKEN",
            accessExpiresIn = 300,
            refreshToken = "REFRESH_TOKEN",
            refreshExpiresIn = 3,
            tokenType = "access_token"
        )
    )
}
