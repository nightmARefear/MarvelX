package com.alexgreece45.marvelx.core.network.storage

import com.alexgreece45.marvelx.model.model.Signature

interface SignatureStorage {
    fun getSignature(): Signature?
    suspend fun setSignature(signature: Signature)
    suspend fun removeSignature()
}