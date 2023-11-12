package com.alexgreece45.marvelx.domain.controllers

import com.alexgreece45.marvelx.data.domains.userAuthData.UserAuthData
import com.google.gson.Gson
import logcat.logcat
import javax.inject.Inject

interface AuthController {
    /**
     * Returns the access token with its type.
     * */
    fun getAccessToken(): String?

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

class AuthControllerImpl @Inject constructor(
    private val prefKeys: PrefKeys
) : AuthController {
    private val userAuthData: UserAuthData?
        get() {
            return try {
                Gson().fromJson(
                    prefKeys.getUserAuthData(),
                    UserAuthData::class.java
                )
            } catch (e: Exception) {
                logcat("AuthController") { "User authentication data is null." }
                null
            }
        }

    override fun getAccessToken(): String? =
        userAuthData?.let { userAuthData ->
            "${userAuthData.tokenType} ${userAuthData.accessToken}"
        }

    override fun getRefreshToken(): String? =
        userAuthData?.refreshToken

    override fun setData(userAuthData: UserAuthData) =
        prefKeys.setUserAuthData(Gson().toJson(userAuthData))

    override fun isAccessTokenExpired(): Boolean =
        userAuthData?.let { userAuthData ->
            (System.currentTimeMillis() / 1000) >= userAuthData.accessExpiresIn
        } ?: true

    override fun isRefreshTokenExpired(): Boolean =
        userAuthData?.let { userAuthData ->
            (System.currentTimeMillis() / 1000) >= userAuthData.refreshExpiresIn
        } ?: true

    override fun removeUserCredentials() {
        prefKeys.removeUserId()
        prefKeys.removeUserAuthData()
    }
}