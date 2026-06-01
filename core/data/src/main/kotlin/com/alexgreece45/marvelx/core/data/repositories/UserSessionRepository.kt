package com.alexgreece45.marvelx.core.data.repositories

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.datastore.data_sources.UserSessionDataSource
import com.alexgreece45.marvelx.model.model.AuthenticationTokens
import com.alexgreece45.marvelx.model.model.UserSession
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface UserSessionRepository {

    suspend fun getUserSession(): UserSession?
    suspend fun hasUserSession(): Boolean
    suspend fun setUserSession(userSession: UserSession)
    suspend fun removeUserSession()

    suspend fun getUserId(): String
    suspend fun setUserId(userId: String)

    suspend fun getAuthenticationTokens(): AuthenticationTokens?
    suspend fun setAuthenticationTokens(authenticationTokens: AuthenticationTokens)

    suspend fun getAccessToken(): String

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
    private val userSessionDataSource: UserSessionDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : UserSessionRepository {

    override suspend fun getUserSession(): UserSession? {
        return withContext(ioDispatcher) {
            userSessionDataSource.getUserSession()
        }
    }

    override suspend fun hasUserSession(): Boolean {
        return getUserSession() != null
    }

    override suspend fun setUserSession(userSession: UserSession) {
        withContext(ioDispatcher) {
            userSessionDataSource.setUserSession { userSession }
        }
    }

    override suspend fun removeUserSession() {
        withContext(ioDispatcher) {
            userSessionDataSource.removeUserSession()
        }
    }

    override suspend fun getUserId(): String {
        return withContext(ioDispatcher) { getUserSession()?.userId.orEmpty() }
    }

    override suspend fun setUserId(userId: String) {
        withContext(ioDispatcher) {
            userSessionDataSource.setUserSession { currentUserSession ->
                currentUserSession.copy(
                    userId = userId
                )
            }
        }
    }

    override suspend fun getAuthenticationTokens(): AuthenticationTokens? {
        return getUserSession()?.authenticationTokens
    }

    override suspend fun setAuthenticationTokens(authenticationTokens: AuthenticationTokens) {
        withContext(ioDispatcher) {
            userSessionDataSource.setUserSession { currentUserSession ->
                currentUserSession.copy(
                    authenticationTokens = authenticationTokens
                )
            }
        }
    }

    override suspend fun getAccessToken(): String {
        return withContext(ioDispatcher) {
            getUserSession()?.authenticationTokens?.accessToken.orEmpty()
        }
    }

    override suspend fun getAccessTokenExpiration(): Long {
        return withContext(ioDispatcher) {
            getUserSession()?.authenticationTokens?.accessExpiresIn ?: 0L
        }
    }

    override suspend fun isAccessTokenExpired(): Boolean {
        return withContext(ioDispatcher) {
            if (getUserSession() != null) {
                DateUtil.getEpochSeconds() >= getAccessTokenExpiration()
            } else {
                true
            }
        }
    }

    override suspend fun getRefreshToken(): String? {
        return withContext(ioDispatcher) {
            getUserSession()?.authenticationTokens?.refreshToken
        }
    }

    override suspend fun getRefreshTokenExpiration(): Long {
        return withContext(ioDispatcher) {
            getUserSession()?.authenticationTokens?.refreshExpiresIn ?: 0L
        }
    }

    override suspend fun isRefreshTokenExpired(): Boolean {
        return withContext(ioDispatcher) {
            if (getUserSession() != null) {
                DateUtil.getEpochSeconds() >= getRefreshTokenExpiration()
            } else {
                true
            }
        }
    }
}