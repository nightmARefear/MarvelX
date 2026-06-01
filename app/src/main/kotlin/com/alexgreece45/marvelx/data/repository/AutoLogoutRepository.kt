package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.datastore.data_sources.AutoLogoutDataSource
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Single
import java.time.Instant
import kotlin.coroutines.CoroutineContext

interface AutoLogoutRepository {

    /**
     * Retrieves whether the application should remember the user's session once logged in. It has a
     * default value of `false`.
     */
    suspend fun getRememberUserSession(): Boolean

    /**
     * Sets whether the application should remember the user's session once logged in.
     *
     * ### Note: During logout, this value is set to `false`.
     */
    suspend fun setRememberUserSession(remember: Boolean)
    suspend fun removeRememberUserSession()

    suspend fun getAutoLogoutTimestamp(): Instant?
    suspend fun setAutoLogoutTimestamp(timestamp: Instant)
    suspend fun removeAutoLogoutTimestamp()

    suspend fun removeAll()
}

@Single
internal class AutoLogoutRepositoryImpl(
    private val autoLogoutDataSource: AutoLogoutDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : AutoLogoutRepository {

    override suspend fun getRememberUserSession(): Boolean {
        return withContext(ioDispatcher) {
            autoLogoutDataSource.getRememberUserSession()
        }
    }

    override suspend fun setRememberUserSession(remember: Boolean) {
        withContext(ioDispatcher) {
            autoLogoutDataSource.setRememberUserSession(remember)
        }
    }

    override suspend fun removeRememberUserSession() {
        withContext(ioDispatcher) {
            autoLogoutDataSource.removeRememberUserSession()
        }
    }

    override suspend fun getAutoLogoutTimestamp(): Instant? {
        return withContext(ioDispatcher) {
            autoLogoutDataSource.getTimestamp()
        }
    }

    override suspend fun setAutoLogoutTimestamp(timestamp: Instant) {
        withContext(ioDispatcher) {
            autoLogoutDataSource.setTimestamp(timestamp)
        }
    }

    override suspend fun removeAutoLogoutTimestamp() {
        withContext(ioDispatcher) {
            autoLogoutDataSource.removeTimestamp()
        }
    }

    override suspend fun removeAll() {
        withContext(ioDispatcher) {
            launch {
                removeRememberUserSession()
            }
            launch {
                removeAutoLogoutTimestamp()
            }
        }
    }
}