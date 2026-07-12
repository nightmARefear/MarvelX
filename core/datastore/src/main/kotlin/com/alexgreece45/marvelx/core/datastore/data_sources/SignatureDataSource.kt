package com.alexgreece45.marvelx.core.datastore.data_sources

import androidx.datastore.core.DataStore
import com.alexgreece45.marvelx.core.datastore.providers.model.SignatureDataStore
import com.alexgreece45.marvelx.core.datastore.extensions.getData
import com.alexgreece45.marvelx.core.datastore.extensions.removeDataCatchable
import com.alexgreece45.marvelx.core.datastore.extensions.updateDataCatchable
import com.alexgreece45.marvelx.core.datastore.mappers.toEntity
import com.alexgreece45.marvelx.core.datastore.mappers.toModel
import com.alexgreece45.marvelx.core.datastore.model.SignatureEntity
import com.alexgreece45.marvelx.model.model.Signature
import org.koin.core.annotation.Single

interface SignatureDataSource {
    suspend fun getSignature(): Signature?
    suspend fun setSignature(signature: Signature)
    suspend fun removeSignature()
}

@Single
internal class SignatureDataSourceImpl(
    @SignatureDataStore private val dataStore: DataStore<SignatureEntity?>,
) : SignatureDataSource {

    override suspend fun getSignature(): Signature? {
        return dataStore.getData()?.toModel()
    }

    override suspend fun setSignature(signature: Signature) {
        dataStore.updateDataCatchable { _ -> signature.toEntity() }
    }

    override suspend fun removeSignature() {
        dataStore.removeDataCatchable()
    }
}