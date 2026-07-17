package com.alexgreece45.marvelx.core.data.storage

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.datastore.data_sources.FcmTokenDataSource
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface FcmTokenStorage {

    fun getFcmToken(): String?
    fun setFcmToken(fcmToken: String)
    fun removeFcmToken()
}

@Single
internal class FcmTokenStorageImpl(
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    private val fcmTokenDataSource: FcmTokenDataSource
) : FcmTokenStorage {

    override fun getFcmToken(): String? {
        return runBlocking(ioDispatcher) { fcmTokenDataSource.getFcmToken() }
    }

    override fun setFcmToken(fcmToken: String) {
        runBlocking(ioDispatcher) { fcmTokenDataSource.setFcmToken(fcmToken) }
    }

    override fun removeFcmToken() {
        runBlocking(ioDispatcher) { fcmTokenDataSource.removeFcmToken() }
    }
}