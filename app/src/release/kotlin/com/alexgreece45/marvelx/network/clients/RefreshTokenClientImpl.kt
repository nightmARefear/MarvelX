package com.alexgreece45.marvelx.network.clients

import com.alexgreece45.marvelx.network.models_api.token.ResponseToken
import com.alexgreece45.marvelx.network.services.RefreshTokenService
import org.koin.core.annotation.Single
import retrofit2.Response

@Single
class RefreshTokenClientImpl(
    private val refreshTokenService: RefreshTokenService
) : RefreshTokenClient {

    override suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Response<ResponseToken> = refreshTokenService.refreshAccessToken(clientId, refreshToken)
}
