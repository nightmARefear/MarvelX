package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import kotlinx.coroutines.flow.first
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
        return handleApiResultNoTransformation(
            apiResult = apiClient.markNotificationAsOpened(),
            ioDispatcher = ioDispatcher
        ).first() is RepositoryResult.Success
    }
}