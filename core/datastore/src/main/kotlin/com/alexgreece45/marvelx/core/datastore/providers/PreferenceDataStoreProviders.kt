package com.alexgreece45.marvelx.core.datastore.providers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.alexgreece45.marvelx.core.datastore.providers.model.AutoLogoutPreferences
import com.alexgreece45.marvelx.core.datastore.providers.model.AutoLogoutPreferencesKeys
import com.alexgreece45.marvelx.core.datastore.providers.model.FcmTokenPreferences
import com.alexgreece45.marvelx.core.datastore.providers.model.FcmTokenPreferencesKeys
import org.koin.core.annotation.Single

@Single
@AutoLogoutPreferences
internal fun provideAutoLogoutPreferencesDataStore(
    context: Context
): DataStore<Preferences> =
    providePreferencesDataStore(
        context = context,
        fileName = AutoLogoutPreferencesKeys.DATA_STORE_FILE_NAME
    )

@Single
@FcmTokenPreferences
internal fun provideFcmTokenPreferencesDataStore(
    context: Context
): DataStore<Preferences> =
    providePreferencesDataStore(
        context = context,
        fileName = FcmTokenPreferencesKeys.DATA_STORE_FILE_NAME
    )

private fun providePreferencesDataStore(
    context: Context,
    fileName: String
): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
        produceFile = {
            context.preferencesDataStoreFile(fileName)
        }
    )