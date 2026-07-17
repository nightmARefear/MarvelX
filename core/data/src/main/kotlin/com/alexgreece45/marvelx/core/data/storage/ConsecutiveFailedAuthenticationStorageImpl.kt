package com.alexgreece45.marvelx.core.data.storage

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.datastore.data_sources.ConsecutiveFailedAuthenticationDataSource
import com.alexgreece45.marvelx.core.network.storage.ConsecutiveFailedAuthenticationStorage
import com.alexgreece45.marvelx.model.model.ConsecutiveFailedAuthentication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single
internal class ConsecutiveFailedAuthenticationStorageImpl(
    private val consecutiveFailedAuthenticationDataSource: ConsecutiveFailedAuthenticationDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + ioDispatcher)
) : ConsecutiveFailedAuthenticationStorage {

    override fun getConsecutiveFailedAuthentication(): ConsecutiveFailedAuthentication? {
        return runBlocking(ioDispatcher) {
            consecutiveFailedAuthenticationDataSource.getConsecutiveFailedAuthentication()
        }
    }

    override fun setConsecutiveFailedAuthentication(consecutiveFailedAuthentication: ConsecutiveFailedAuthentication) {
        scope.launch {
            consecutiveFailedAuthenticationDataSource.setConsecutiveFailedAuthentication(
                consecutiveFailedAuthentication
            )
        }
    }

    override fun removeConsecutiveFailedAuthentication() {
        scope.launch {
            consecutiveFailedAuthenticationDataSource.removeConsecutiveFailedAuthentication()
        }
    }
}