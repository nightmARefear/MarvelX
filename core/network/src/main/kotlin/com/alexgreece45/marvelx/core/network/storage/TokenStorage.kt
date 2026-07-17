package com.alexgreece45.marvelx.core.network.storage

import com.alexgreece45.marvelx.model.model.AuthenticationTokens

/**
 * Token handling for the network layer.
 */
interface TokenStorage {
    fun getAccessTokenAndType(): String
    fun getRefreshToken(): String?
    fun setAuthenticationTokens(authenticationTokens: AuthenticationTokens)
}