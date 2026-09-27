package com.alexgreece45.marvelx.core.network.storage

import com.alexgreece45.marvelx.model.model.AuthenticationTokens

/**
 * The single entry point for refreshing the access token.
 *
 * Refreshes are single-flight: concurrent callers (the periodic worker, the OkHttp authenticator,
 * the websocket reconnection, the cold-start login) share one in-flight request instead of each
 * spending the same refresh token. The new tokens are persisted before any caller is resumed, so
 * a rotated refresh token can never be lost between the response and the write.
 */
interface TokenRefresher {
    suspend fun refresh(): TokenRefreshResult
}

sealed interface TokenRefreshResult {
    data class Success(val authenticationTokens: AuthenticationTokens) : TokenRefreshResult

    /**
     * The backend rejected the refresh (or returned no body), or there is no refresh token stored.
     */
    data class Failed(val reason: String) : TokenRefreshResult

    /**
     * The refresh couldn't be performed, e.g., due to a network error.
     */
    data class Error(val throwable: Throwable) : TokenRefreshResult
}
