package com.alexgreece45.marvelx.core.datastore.providers.factory

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import androidx.datastore.tink.AeadSerializer
import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.datastore.providers.model.ProviderPath
import com.google.crypto.tink.Aead
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single
internal class EncryptedDataStoreFactory(
    private val context: Context,
    private val aead: Aead,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) {

    fun <T> create(
        serializer: Serializer<T?>,
        providerPath: ProviderPath
    ): DataStore<T?> {

        val aeadSerializer = AeadSerializer(
            aead = aead,
            wrappedSerializer = serializer,
            associatedData = providerPath.fileName.encodeToByteArray(),
        )

        return DataStoreFactory.create(
            serializer = aeadSerializer,
            scope = CoroutineScope(SupervisorJob() + ioDispatcher),
            produceFile = { context.dataStoreFile(providerPath.fileName) }
        )
    }
}