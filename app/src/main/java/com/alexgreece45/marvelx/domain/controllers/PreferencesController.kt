package com.alexgreece45.marvelx.domain.controllers

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.or
import logcat.logcat
import javax.inject.Inject
import javax.inject.Singleton

interface PreferencesController {
    fun getString(key: String): String

    fun setString(key: String, value: String)

    fun getUserToken(): String

    fun setUserToken(token: String)
}

class PreferencesControllerImpl @Inject constructor(
    private val resourceProvider: ResourceProvider
) : PreferencesController {
    private val sharedPreferences: SharedPreferences
        get() = getEncryptedSharedPreferences()

    override fun getString(key: String): String =
        sharedPreferences.getString(key, "").or("")

    override fun setString(key: String, value: String) =
        sharedPreferences.edit().putString(key, value).apply()

    override fun getUserToken(): String =
        getString(BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_TOKEN)

    override fun setUserToken(token: String) =
        setString(
            BuildConfig.ENCRYPTED_SHARED_PREFERENCES_KEY_USER_TOKEN,
            token
        ).also { logcat { "userToken = ${getUserToken()}" } }

    private fun getEncryptedSharedPreferences(): SharedPreferences = EncryptedSharedPreferences.create(
        BuildConfig.ENCRYPTED_SHARED_PREFERENCES_FILE_NAME,
        MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
        resourceProvider.getContext(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}