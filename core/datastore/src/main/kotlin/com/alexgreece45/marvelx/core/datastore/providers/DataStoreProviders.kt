package com.alexgreece45.marvelx.core.datastore.providers

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.datastore.tink.AeadSerializer
import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.datastore.model.ConsecutiveFailedAuthenticationEntity
import com.alexgreece45.marvelx.core.datastore.model.SignatureEntity
import com.alexgreece45.marvelx.core.datastore.model.UserSessionEntity
import com.alexgreece45.marvelx.core.datastore.providers.factory.EncryptedDataStoreFactory
import com.alexgreece45.marvelx.core.datastore.providers.model.ProviderPath
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplate
import com.google.crypto.tink.KeysetHandle
import com.google.crypto.tink.RegistryConfiguration
import com.google.crypto.tink.aead.PredefinedAeadParameters
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single
internal fun provideUserSessionDataStore(
    serializer: Serializer<UserSessionEntity?>,
    encryptedDataStoreFactory: EncryptedDataStoreFactory
): DataStore<UserSessionEntity?> {
    return encryptedDataStoreFactory.create(
        serializer = serializer,
        providerPath = ProviderPath.USER_SESSION
    )
}

@Single
internal fun provideSignatureDataStore(
    serializer: Serializer<SignatureEntity?>,
    encryptedDataStoreFactory: EncryptedDataStoreFactory
): DataStore<SignatureEntity?> {
    return encryptedDataStoreFactory.create(
        serializer = serializer,
        providerPath = ProviderPath.SIGNATURE
    )
}

@Single
internal fun provideConsecutiveFailedAuthenticationDataStore(
    serializer: Serializer<ConsecutiveFailedAuthenticationEntity?>,
    encryptedDataStoreFactory: EncryptedDataStoreFactory
): DataStore<ConsecutiveFailedAuthenticationEntity?> {
    return encryptedDataStoreFactory.create(
        serializer = serializer,
        providerPath = ProviderPath.CONSECUTIVE_FAILED_AUTHENTICATION
    )
}

private fun <T : Any> generalProviderDataStore(
    context: Context,
    ioDispatcher: CoroutineContext,
    serializer: Serializer<T?>,
    providerPath: ProviderPath
): DataStore<T?> {
    return DataStoreFactory.create(
        serializer = serializer,
        scope = CoroutineScope(SupervisorJob() + ioDispatcher),
        produceFile = { context.dataStoreFile(providerPath.fileName) }
    )
}

private fun getKeysetHandle(context: Context): KeysetHandle =
    AndroidKeysetManager.Builder()
        .withSharedPref(context, "keyset", "keyset_prefs")
        .withKeyTemplate(KeyTemplate.createFrom(PredefinedAeadParameters.AES256_GCM))
        .withMasterKeyUri("android-keystore://master_key")
        .build()
        .keysetHandle

private fun <T> generalProviderEncryptedDataStore(
    context: Context,
    ioDispatcher: CoroutineContext,
    serializer: Serializer<T?>,
    providerPath: ProviderPath
): DataStore<T?> {
    val aeadSerializer = AeadSerializer(
        aead =
            getKeysetHandle(context).getPrimitive(
                RegistryConfiguration.get(),
                Aead::class.java,
            ),
        wrappedSerializer = serializer,
        associatedData = providerPath.fileName.encodeToByteArray(),
    )

    return DataStoreFactory.create(
        serializer = aeadSerializer,
        scope = CoroutineScope(SupervisorJob() + ioDispatcher),
        produceFile = { context.dataStoreFile(providerPath.fileName) }
    )
}