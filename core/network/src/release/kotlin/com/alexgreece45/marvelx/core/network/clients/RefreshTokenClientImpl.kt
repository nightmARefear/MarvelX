package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.token.ResponseToken
import com.alexgreece45.marvelx.core.network.services.RefreshTokenService
import org.koin.core.annotation.Single
import retrofit2.Response

@Single
internal class RefreshTokenClientImpl(
    private val refreshTokenService: RefreshTokenService
) : RefreshTokenClient {

    override suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Response<ResponseToken> = refreshTokenService.refreshAccessToken(clientId, refreshToken)
}