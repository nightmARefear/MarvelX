package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.network.storage.TokenRefreshResult
import com.alexgreece45.marvelx.core.network.storage.TokenRefresher
import org.koin.core.annotation.Single

interface RefreshTokenRepository {

    /**
     * Refreshes the access token with the stored refresh token, and stores the new tokens. Shares
     * any refresh that is already in flight (see [TokenRefresher]).
     */
    suspend fun refreshAccessToken(): TokenRefreshResult
}

@Single
class RefreshTokenRepositoryImpl(
    private val tokenRefresher: TokenRefresher
) : RefreshTokenRepository {

    override suspend fun refreshAccessToken(): TokenRefreshResult = tokenRefresher.refresh()
}
