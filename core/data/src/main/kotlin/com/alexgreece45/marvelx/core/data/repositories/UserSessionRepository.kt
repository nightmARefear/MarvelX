package com.alexgreece45.marvelx.core.data.repositories

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.datastore.data_sources.AuthenticationTokensDataSource
import com.alexgreece45.marvelx.core.datastore.data_sources.UserIdDataSource
import com.alexgreece45.marvelx.model.model.AuthenticationTokens
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface UserSessionRepository {
    suspend fun setUserSession(userId: String, authenticationTokens: AuthenticationTokens)
    suspend fun hasUserSession(): Boolean

    suspend fun getUserId(): String
    suspend fun setUserId(userId: String)
    suspend fun removeUserId()

    suspend fun setAuthenticationTokens(authenticationTokens: AuthenticationTokens)
    suspend fun removeAuthenticationTokens()

    suspend fun getAccessToken(): String
    suspend fun getAccessTokenAndType(): String

    /**
     * Returns the expiration UNIX of the access token.
     *
     * If the access token does not exist, `0` is returned.
     */
    suspend fun getAccessTokenExpiration(): Long

    suspend fun isAccessTokenExpired(): Boolean

    suspend fun getRefreshToken(): String?

    /**
     * Returns the expiration UNIX of the refresh token.
     *
     * If the refresh token does not exist, `0` is returned.
     */
    suspend fun getRefreshTokenExpiration(): Long

    suspend fun isRefreshTokenExpired(): Boolean
}

@Single
internal class UserSessionRepositoryImpl(
    private val userIdDataSource: UserIdDataSource,
    private val authenticationTokensDataSource: AuthenticationTokensDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : UserSessionRepository {

    override suspend fun hasUserSession(): Boolean = withContext(ioDispatcher) {
        userIdDataSource.getUserId() != null
                && authenticationTokensDataSource.getAuthenticationTokens() != null
    }

    override suspend fun setUserSession(
        userId: String,
        authenticationTokens: AuthenticationTokens
    ) {
        withContext(ioDispatcher) {
            launch {
                setUserId(userId)
            }

            launch {
                setAuthenticationTokens(authenticationTokens)
            }
        }
    }

    override suspend fun getUserId(): String = withContext(ioDispatcher) {
        userIdDataSource.getUserId().orEmpty()
    }

    override suspend fun setUserId(userId: String) {
        withContext(ioDispatcher) {
            userIdDataSource.setUserId(userId)
        }
    }

    override suspend fun removeUserId() {
        withContext(ioDispatcher) {
            userIdDataSource.removeUserId()
        }
    }

    override suspend fun setAuthenticationTokens(authenticationTokens: AuthenticationTokens) {
        withContext(ioDispatcher) {
            authenticationTokensDataSource.setAuthenticationTokens(authenticationTokens)
        }
    }

    override suspend fun removeAuthenticationTokens() {
        withContext(ioDispatcher) {
            authenticationTokensDataSource.removeAuthenticationTokens()
        }
    }

    override suspend fun getAccessToken(): String = withContext(ioDispatcher) {
        getAuthenticationTokens()?.accessToken.orEmpty()
    }

    override suspend fun getAccessTokenAndType(): String = withContext(ioDispatcher) {
        getAuthenticationTokens()?.accessTokenAndType.orEmpty()
    }

    override suspend fun getAccessTokenExpiration(): Long = withContext(ioDispatcher) {
        getAuthenticationTokens()?.accessExpiresIn ?: 0L
    }

    override suspend fun isAccessTokenExpired(): Boolean = withContext(ioDispatcher) {
        DateUtil.getEpochSeconds() >= getAccessTokenExpiration()
    }

    override suspend fun getRefreshToken(): String? = withContext(ioDispatcher) {
        getAuthenticationTokens()?.refreshToken
    }

    override suspend fun getRefreshTokenExpiration(): Long = withContext(ioDispatcher) {
        getAuthenticationTokens()?.refreshExpiresIn ?: 0L
    }

    override suspend fun isRefreshTokenExpired(): Boolean = withContext(ioDispatcher) {
        DateUtil.getEpochSeconds() >= getRefreshTokenExpiration()
    }

    private suspend fun getAuthenticationTokens(): AuthenticationTokens? =
        withContext(ioDispatcher) {
            authenticationTokensDataSource.getAuthenticationTokens()
        }
}