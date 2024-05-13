package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.data.domains.userAuthData.UserAuthData
import com.alexgreece45.marvelx.util.DateUtil
import com.google.gson.Gson

interface AuthController {
    /**
     * Returns the access token with its type.
     * */
    fun getAccessToken(): String?

    /**
     * Returns the expiration UNIX of the access token.
     *
     * If the access token does not exist, `0` is returned.
     */
    fun getAccessTokenExpiration(): Long

    /**
     * Returns the expiration UNIX of the refresh token.
     *
     * If the refresh token does not exist, `0` is returned.
     */
    fun getRefreshTokenExpiration(): Long

    /**
     * Returns the refresh token.
     * */
    fun getRefreshToken(): String?

    fun setData(userAuthData: UserAuthData)

    fun isAccessTokenExpired(): Boolean
    fun isRefreshTokenExpired(): Boolean

    /**
     * Removes the stored user credentials: userId, and UserAuthData.
     * ### Note: This operation is irreversible, and it MUST be used with caution.
     * */
    fun removeUserCredentials()
}

class AuthControllerImpl(
    private val prefKeys: PrefKeys,
    private val logger: AppLoggerController
) : AuthController {
    private val userAuthData: UserAuthData?
        get() {
            return try {
                Gson().fromJson(
                    prefKeys.getUserAuthData(),
                    UserAuthData::class.java
                )
            } catch (e: Exception) {
                logger.d("AuthController") { "User authentication data is null." }
                null
            }
        }

    override fun getAccessToken(): String? =
        userAuthData?.let { userAuthData ->
            "${userAuthData.tokenType} ${userAuthData.accessToken}"
        }

    override fun getAccessTokenExpiration(): Long =
        userAuthData?.accessExpiresIn ?: 0L

    override fun getRefreshTokenExpiration(): Long =
        userAuthData?.refreshExpiresIn ?: 0L

    override fun getRefreshToken(): String? =
        userAuthData?.refreshToken

    override fun setData(userAuthData: UserAuthData) =
        prefKeys.setUserAuthData(Gson().toJson(userAuthData))

    override fun isAccessTokenExpired(): Boolean =
        if (userAuthData != null) {
            DateUtil.getSecondsFromEpoch() >= getAccessTokenExpiration()
        } else {
            true
        }

    override fun isRefreshTokenExpired(): Boolean =
        if (userAuthData != null) {
            DateUtil.getSecondsFromEpoch() >= getRefreshTokenExpiration()
        } else {
            true
        }

    override fun removeUserCredentials() {
        prefKeys.removeUserId()
        prefKeys.removeUserAuthData()
    }
}