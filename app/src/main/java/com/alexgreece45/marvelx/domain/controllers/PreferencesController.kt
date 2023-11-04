package com.alexgreece45.marvelx.domain.controllers

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.or
import logcat.logcat
import javax.inject.Inject

interface PreferencesController {
    /**
     * Returns true if the provided [key] exists
     * in the [SharedPreferences], otherwise false.
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
     * @param key the preference key to be removed
     * */
    fun remove(key: String)

    /**
     * Removes all the keys from the [SharedPreferences].
     *
     * ### Note: This operation should be used with caution as it is irreversible!
     * */
    fun removeAll()
}

class PreferencesControllerImpl @Inject constructor(
    private val resourceProvider: ResourceProvider
) : PreferencesController {
    private val sharedPreferences: SharedPreferences
        get() = getEncryptedSharedPreferences()

    override fun containsKey(key: String): Boolean =
        sharedPreferences.contains(key)

    override fun getString(key: String, defaultValue: String): String =
        sharedPreferences.getString(key, defaultValue).or(defaultValue)

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
     * ### Note: This function MUST only be used internally, to obtain an object of [SharedPreferences].
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
    fun containsUserToken(): Boolean
    fun containsUserCredentials(): Boolean

    fun getUserId(): Int
    fun getUserToken(): String

    fun setUserId(userId: Int)
    fun setUserToken(userToken: String)

    fun removeUserId()
    fun removeUserToken()

    /**
     * Removes the stored user credentials (i.e., userId, token).
     *
    ### Note: This operation should be used with caution as it is irreversible!
     * */
    fun removeUserCredentials()
}

class PrefKeysImpl @Inject constructor(
    private val preferencesController: PreferencesController
) : PrefKeys {

    override fun containsUserId(): Boolean =
        preferencesController.containsKey(BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_ID)

    override fun containsUserToken(): Boolean =
        preferencesController.containsKey(BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_TOKEN)

    override fun containsUserCredentials(): Boolean = containsUserId() && containsUserToken()

    override fun getUserId(): Int =
        preferencesController.getInt(BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_ID, -1)

    override fun getUserToken(): String =
        preferencesController.getString(BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_TOKEN, "")

    override fun setUserId(userId: Int) =
        preferencesController.setInt(
            BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_ID,
            userId
        ).also { logcat { "(setUserId) user_id = $userId" } }

    override fun setUserToken(userToken: String) =
        preferencesController.setString(
            BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_TOKEN,
            userToken
        ).also { logcat { "(setUserToken) user_token = $userToken" } }

    override fun removeUserId() =
        preferencesController.remove(BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_ID)

    override fun removeUserToken() =
        preferencesController.remove(BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_TOKEN)

    override fun removeUserCredentials() {
        removeUserId()
        removeUserToken()
    }
}