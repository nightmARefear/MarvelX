package com.alexgreece45.marvelx.core.data.storage

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.datastore.data_sources.SignatureDataSource
import com.alexgreece45.marvelx.core.network.storage.SignatureStorage
import com.alexgreece45.marvelx.model.model.Signature
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single
internal class SignatureStorageImpl(
    private val signatureDataSource: SignatureDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : SignatureStorage {

    override fun getSignature(): Signature? {
        return runBlocking(ioDispatcher) { signatureDataSource.getSignature() }
    }

    override suspend fun setSignature(signature: Signature) {
        withContext(ioDispatcher) {
            signatureDataSource.setSignature(signature)
        }
    }

    override suspend fun removeSignature() {
        withContext(ioDispatcher) {
            signatureDataSource.removeSignature()
        }
    }
}