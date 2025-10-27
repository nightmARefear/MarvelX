package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.network_logic.clients.RefreshTokenClient
import com.alexgreece45.marvelx.network_logic.models_api.token.ResponseToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface RefreshTokenRepository {

    /**
     * Refreshes the access token.
     */
    suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Flow<TokenRefreshRepositoryResult>
}

class RefreshTokenRepositoryImpl(
    private val refreshTokenClient: RefreshTokenClient
) : RefreshTokenRepository {
    override suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Flow<TokenRefreshRepositoryResult> = flow {
        refreshTokenClient.refreshAccessToken(clientId, refreshToken).let { response ->
            if (response.isSuccessful) {
                emit(
                    TokenRefreshRepositoryResult.Success(responseToken = response.body())
                )
            } else {
                emit(
                    TokenRefreshRepositoryResult.Failed(
                        failureMessage = response.errorBody()?.string().orEmpty()
                    )
                )
            }
        }
    }
}

sealed interface TokenRefreshRepositoryResult {
    data class Success(val responseToken: ResponseToken?) : TokenRefreshRepositoryResult
    data class Failed(val failureMessage: String) : TokenRefreshRepositoryResult
}
