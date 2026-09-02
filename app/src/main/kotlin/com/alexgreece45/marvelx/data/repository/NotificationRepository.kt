package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface NotificationRepository {

    suspend fun markIncomingFriendRequestAsRead(): Boolean
}

@Single
class NotificationRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : NotificationRepository {

    override suspend fun markIncomingFriendRequestAsRead(): Boolean {
        return handleApiResultNoTransformationNoFlow(
            apiCall = apiClient::markNotificationAsOpened,
            ioDispatcher = ioDispatcher
        ) is RepositoryResult.Success
    }
}