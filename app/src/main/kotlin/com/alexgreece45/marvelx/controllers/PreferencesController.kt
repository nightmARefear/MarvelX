package com.alexgreece45.marvelx.controllers

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.domain.model_api.user.postLogin.SignData
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.constants.ConstantsPrefs
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.time.Instant
import java.time.format.DateTimeParseException

interface PreferencesController {
    /**
     * Returns true if the provided [key] exists in the [SharedPreferences], otherwise false.
     * */
    fun containsKey(key: String): Boolean

    fun getString(key: String, defaultValue: String): String
    fun getInt(key: String, defaultValue: Int): Int
    fun getLong(key: String, defaultValue: Long): Long
    fun getBoolean(key: String, defaultValue: Boolean): Boolean

    fun setString(key: String, value: String)
    fun setInt(key: String, value: Int)
    fun setLong(key: String, value: Long)
    fun setBoolean(key: String, value: Boolean)

    /**
     * Removes the provided [key] from the [SharedPreferences].
     *
     * @param key The preference key to be removed.
     * */
    fun remove(key: String)

    /**
     * Removes all the keys from the [SharedPreferences].
     *
     * ### Note: This operation should be used with caution as it is irreversible!
     * */
    fun removeAll()
}

class PreferencesControllerImpl(
    private val resourceProvider: ResourceProvider
) : PreferencesController {
    private val sharedPreferences: SharedPreferences
        get() = getEncryptedSharedPreferences()

    override fun containsKey(key: String): Boolean =
        sharedPreferences.contains(key)

    override fun getString(key: String, defaultValue: String): String =
        sharedPreferences.getString(key, defaultValue) ?: defaultValue

    override fun getInt(key: String, defaultValue: Int): Int =
        sharedPreferences.getInt(key, defaultValue)

    override fun getLong(key: String, defaultValue: Long): Long =
        sharedPreferences.getLong(key, defaultValue)

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        sharedPreferences.getBoolean(key, defaultValue)


    override fun setString(key: String, value: String) =
        sharedPreferences.edit().putString(key, value).apply()

    override fun setInt(key: String, value: Int) =
        sharedPreferences.edit().putInt(key, value).apply()

    override fun setLong(key: String, value: Long) =
        sharedPreferences.edit().putLong(key, value).apply()

    override fun setBoolean(key: String, value: Boolean) =
        sharedPreferences.edit().putBoolean(key, value).apply()

    override fun remove(key: String) =
        sharedPreferences.edit().remove(key).apply()

    override fun removeAll() =
        sharedPreferences.edit().clear().apply()

    /**
     * Returns an instance of [SharedPreferences], with the appropriate
     * configurations.
     *
     * ### Note: This function MUST only be used internally.
     *
     * @return Returns an instance of [SharedPreferences].
     * */
    private fun getEncryptedSharedPreferences(): SharedPreferences =
        EncryptedSharedPreferences.create(
            BuildConfig.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            resourceProvider.getContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
}

interface PrefKeys {
    fun containsUserId(): Boolean
    fun containsUserCredentials(): Boolean
    fun containsUserAuthData(): Boolean

    fun getUserId(): Int
    fun getUserAuthData(): String

    /**
     * Retrieves whether the application should remember the user's session once logged in. It has a
     * default value of `false`.
     */
    fun shouldRememberUserSession(): Boolean

    /**
     * Retrieves whether the user is logged in. It has a default value of `false`.
     */
    fun isUserLoggedIn(): Boolean

    fun setUserId(userId: Int)
    fun setUserAuthData(userAuthData: String)

    /**
     * Sets whether the application should remember the user's session once logged in.
     *
     * ### Note: During logout, this value is set to `false`.
     */
    fun setRememberUserSession(shouldRemember: Boolean)

    /**
     * Sets whether the user is logged in.
     *
     * ### Note: During logout, this value is set to `false`.
     */
    fun setUserLoggedIn(isLoggedIn: Boolean)

    fun removeUserId()

    /**
     * Removes the stored user authentication data (i.e., access token, refresh token, etc.).
     *
     * ### Note: This operation is irreversible, and it MUST be used with caution.
     * */
    fun removeUserAuthData()

    /**
     * Auto logout timestamp.
     */
    var autoLogoutTimestamp: Instant?

    /**
     * Signing data for requests.
     */
    var signData: SignData?

    /**
     * The KeyStore alias for the oauth key.
     */
    var oauthAlias: String?
}

class PrefKeysImpl(
    private val preferencesController: PreferencesController,
    private val logger: AppLogger
) : PrefKeys {

    override fun containsUserId(): Boolean =
        preferencesController.containsKey(BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_ID)

    override fun containsUserCredentials(): Boolean = containsUserId() && containsUserAuthData()
    override fun containsUserAuthData(): Boolean =
        preferencesController.containsKey("UserAuthData")

    override fun getUserId(): Int =
        preferencesController.getInt(BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_ID, -1)

    override fun getUserAuthData(): String =
        preferencesController.getString("UserAuthData", "{}")

    override fun shouldRememberUserSession(): Boolean =
        preferencesController.getBoolean("rememberMe", false)

    override fun isUserLoggedIn(): Boolean =
        preferencesController.getBoolean("isUserLoggedIn", false)

    override fun setUserId(userId: Int) =
        preferencesController.setInt(
            BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_ID,
            userId
        ).also { logger.d { "(setUserId) user_id = $userId" } }

    override fun setUserAuthData(userAuthData: String) =
        preferencesController.setString(
            key = "UserAuthData",
            value = userAuthData
        )

    override fun setRememberUserSession(shouldRemember: Boolean) =
        preferencesController.setBoolean("rememberMe", shouldRemember)

    override fun setUserLoggedIn(isLoggedIn: Boolean) =
        preferencesController.setBoolean("isUserLoggedIn", isLoggedIn)

    override fun removeUserId() =
        preferencesController.remove(BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_ID)

    override fun removeUserAuthData() =
        preferencesController.remove("UserAuthData")

    override var autoLogoutTimestamp: Instant?
        get() = try {
            Instant.parse(
                preferencesController.getString(
                    ConstantsPrefs.AUTO_LOGOUT_TIMESTAMP,
                    null.toString()
                )
            )
        } catch (e: DateTimeParseException) {
            logger.e("PrefKeys") { "Auto logout timestamp is null." }
            null
        }
        set(value) {
            if (value == null) preferencesController.remove(ConstantsPrefs.AUTO_LOGOUT_TIMESTAMP)
            else preferencesController.setString(
                ConstantsPrefs.AUTO_LOGOUT_TIMESTAMP,
                Gson().toJson(value.toString()).replace("\"", "")
            )
        }

    override var signData: SignData?
        get() = try {
            Gson().fromJson(
                preferencesController.getString(ConstantsPrefs.SIGN_DATA, null.toString()),
                SignData::class.java
            )
        } catch (e: JsonSyntaxException) {
            logger.e("PreferencesController") { "Sign data is null." }
            null
        }
        set(value) {
            if (value == null) preferencesController.remove(ConstantsPrefs.SIGN_DATA)
            else preferencesController.setString(
                ConstantsPrefs.SIGN_DATA,
                Gson().toJson(value)
            )
        }
    override var oauthAlias: String?
        get() = preferencesController.getString(
            ConstantsPrefs.OAUTH_KEYSTORE_ALIAS,
            ""
        )
        set(value) {
            if (value == null) preferencesController.remove(ConstantsPrefs.OAUTH_KEYSTORE_ALIAS)
            else preferencesController.getString(
                ConstantsPrefs.OAUTH_KEYSTORE_ALIAS,
                ""
            )
        }
}