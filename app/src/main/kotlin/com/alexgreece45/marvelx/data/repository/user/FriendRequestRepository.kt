package com.alexgreece45.marvelx.data.repository.user

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.user.incomingFriendRequest.ResponseIncomingFriendRequest
import com.alexgreece45.marvelx.model.model.user.UserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface FriendRequestRepository {

    /**
     * Returns the incoming friend requests of the current user.
     */
    suspend fun getIncomingFriendRequests(): Flow<RepositoryResult<List<ResponseIncomingFriendRequest>>>
    suspend fun sendFriendRequest(userId: UserId): Flow<Boolean>
    suspend fun acceptFriendRequest(userId: UserId): Flow<Boolean>
    suspend fun deleteFriendRequest(userId: UserId, type: String): Flow<Boolean>
}

@Single
class FriendRequestRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : FriendRequestRepository {

    override suspend fun getIncomingFriendRequests(): Flow<RepositoryResult<List<ResponseIncomingFriendRequest>>> =
        flow {
            emitAll(
                handleApiResultNoTransformationWithData(
                    apiResult = apiClient.getIncomingFriendRequests(),
                    ioDispatcher = ioDispatcher
                )
            )
        }

    override suspend fun sendFriendRequest(userId: UserId): Flow<Boolean> = flow {
        handleApiResultNoTransformation(
            apiResult = apiClient.sendFriendRequest(userId.value),
            ioDispatcher = ioDispatcher
        ).collect { result ->
            emit(result is RepositoryResult.Success)
        }
    }

    override suspend fun acceptFriendRequest(userId: UserId): Flow<Boolean> = flow {
        handleApiResultNoTransformation(
            apiResult = apiClient.acceptFriendRequest(userId.value),
            ioDispatcher = ioDispatcher
        ).collect { result ->
            emit(result is RepositoryResult.Success)
        }
    }

    override suspend fun deleteFriendRequest(userId: UserId, type: String): Flow<Boolean> = flow {
        handleApiResultNoTransformation(
            apiResult = apiClient.deleteFriendRequest(userId.value, type),
            ioDispatcher = ioDispatcher
        ).collect { result ->
            emit(result is RepositoryResult.Success)
        }
    }
}