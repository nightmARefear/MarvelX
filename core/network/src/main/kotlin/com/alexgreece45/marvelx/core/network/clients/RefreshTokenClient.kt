package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.model.api.token.ResponseToken
import retrofit2.Response

interface RefreshTokenClient {
    /**
     * Refreshes the access token.
     */
    suspend fun refreshAccessToken(clientId: String, refreshToken: String): Response<ResponseToken>
}