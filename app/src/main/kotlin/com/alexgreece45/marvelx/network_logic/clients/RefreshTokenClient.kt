package com.alexgreece45.marvelx.network_logic.clients

import com.alexgreece45.marvelx.network_logic.models_api.token.ResponseToken
import com.alexgreece45.marvelx.network_logic.services.RefreshTokenService
import retrofit2.Response

interface RefreshTokenClient {
    /**
     * Refreshes the access token.
     */
    suspend fun refreshAccessToken(clientId: String, refreshToken: String): Response<ResponseToken>
}

class RefreshTokenClientImpl(
    private val refreshTokenService: RefreshTokenService
) : RefreshTokenClient {
    override suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Response<ResponseToken> = refreshTokenService.refreshAccessToken(clientId, refreshToken)
}
