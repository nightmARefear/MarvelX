package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.token.ResponseToken
import org.koin.core.annotation.Single
import retrofit2.Response

@Single
internal class RefreshTokenClientImpl : RefreshTokenClient {

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