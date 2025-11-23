package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.controllers.model.auth.CurrentConsecutiveFailedAuthenticationData
import com.alexgreece45.marvelx.domain.model_api.userAuthData.UserAuthData
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.constants.ConstantsPrefs
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

/**
 * This controller contains all authentication related data such as the access token, when the last
 * failed authentication attempt occurred, and more.
 */

interface AuthController {

    /**
     * Returns the access token with its type.
     * */
    fun getAccessToken(): String

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
     * Stores data about consecutive failed authentication attempts, including the timestamp
     * of the most recent failed attempt and the current number of consecutive failures.
     *
     * This should be initialized to `null` when the app first starts.
     */
    var currentConsecutiveFailedAuthenticationData: CurrentConsecutiveFailedAuthenticationData?

    /**
     * Removes the stored user credentials: userId, [UserAuthData], and SignData.
     *
     * ### Note: This operation is irreversible, and it MUST be used with caution.
     * */
    fun removeUserCredentials()
}

class AuthControllerImpl(
    private val logger: AppLogger,
    private val prefKeys: PrefKeys,
    private val preferencesController: PreferencesController,
    private val gson: Gson
) : AuthController {

    private val userAuthData: UserAuthData?
        get() {
            return try {
                gson.fromJson(
                    prefKeys.getUserAuthData(),
                    UserAuthData::class.java
                )
            } catch (_: JsonSyntaxException) {
                logger.e("AuthController") { "User authentication data is null." }
                null
            }
        }

    override fun getAccessToken(): String =
        userAuthData?.let { userAuthData ->
            "${userAuthData.tokenType} ${userAuthData.accessToken}"
        }.orEmpty()

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
            DateUtil.getEpochSeconds() >= getAccessTokenExpiration()
        } else {
            true
        }

    override fun isRefreshTokenExpired(): Boolean =
        if (userAuthData != null) {
            DateUtil.getEpochSeconds() >= getRefreshTokenExpiration()
        } else {
            true
        }

    override var currentConsecutiveFailedAuthenticationData: CurrentConsecutiveFailedAuthenticationData?
        get() = try {
            gson.fromJson(
                preferencesController.getString(
                    ConstantsPrefs.CURRENT_CONSECUTIVE_FAILED_AUTHENTICATION_DATA,
                    null.toString()
                ),
                CurrentConsecutiveFailedAuthenticationData::class.java
            )
        } catch (e: Exception) {
            logger.e("AuthController") { "An exception occurred whilst parsing the current consecutive failed authentication data:\n ${e.localizedMessage.orEmpty()}" }
            null
        }
        set(value) {
            if (value == null) {
                preferencesController.remove(ConstantsPrefs.CURRENT_CONSECUTIVE_FAILED_AUTHENTICATION_DATA)
            } else {
                preferencesController.setString(
                    ConstantsPrefs.CURRENT_CONSECUTIVE_FAILED_AUTHENTICATION_DATA,
                    gson.toJson(value)
                )
            }
        }

    override fun removeUserCredentials() {
        prefKeys.removeUserId()
        prefKeys.removeUserAuthData()
        prefKeys.signData = null
    }
}
