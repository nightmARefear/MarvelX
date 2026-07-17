package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.network.clients.RefreshTokenClient
import com.alexgreece45.marvelx.core.network.model.api.token.ResponseToken
import org.koin.core.annotation.Single

interface RefreshTokenRepository {

    /**
     * Refreshes the access token.
     */
    suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): TokenRefreshRepositoryResult
}

@Single
class RefreshTokenRepositoryImpl(
    private val refreshTokenClient: RefreshTokenClient
) : RefreshTokenRepository {

    override suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): TokenRefreshRepositoryResult {
        return refreshTokenClient.refreshAccessToken(clientId, refreshToken).let { response ->
            if (response.isSuccessful) {
                TokenRefreshRepositoryResult.Success(responseToken = response.body())
            } else {
                TokenRefreshRepositoryResult.Failed(
                    failureMessage = response.errorBody()?.string().orEmpty()
                )
            }
        }
    }
}

sealed interface TokenRefreshRepositoryResult {
    data class Success(val responseToken: ResponseToken?) : TokenRefreshRepositoryResult
    data class Failed(val failureMessage: String) : TokenRefreshRepositoryResult
}
