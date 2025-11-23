package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.clients.ApiClient
import kotlinx.coroutines.flow.first
import kotlin.coroutines.CoroutineContext

interface NotificationRepository {

    suspend fun markIncomingFriendRequestAsRead(): Boolean
}

class NotificationRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : NotificationRepository {

    override suspend fun markIncomingFriendRequestAsRead(): Boolean {
        return handleApiResultNoTransformation(
            apiResult = apiClient.markNotificationAsOpened(),
            ioDispatcher = ioDispatcher
        ).first() is RepositoryResult.Success
    }
}
