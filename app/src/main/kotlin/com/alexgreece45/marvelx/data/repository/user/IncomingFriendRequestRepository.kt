package com.alexgreece45.marvelx.data.repository.user

import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.clients.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.user.incomingFriendRequest.ResponseIncomingFriendRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

interface IncomingFriendRequestRepository {

    /**
     * Returns the incoming friend requests of the current user.
     */
    suspend fun getIncomingFriendRequests(): Flow<RepositoryResult<List<ResponseIncomingFriendRequest>>>
    suspend fun sendFriendRequest(userId: String): Flow<Boolean>
    suspend fun acceptFriendRequest(userId: String): Flow<Boolean>
    suspend fun deleteFriendRequest(userId: String): Flow<Boolean>
}

class IncomingFriendRequestRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : IncomingFriendRequestRepository {

    override suspend fun getIncomingFriendRequests(): Flow<RepositoryResult<List<ResponseIncomingFriendRequest>>> =
        flow {
            emitAll(
                handleApiResultNoTransformationWithData(
                    apiResult = apiClient.getIncomingFriendRequests(),
                    ioDispatcher = ioDispatcher
                )
            )
        }

    override suspend fun sendFriendRequest(userId: String): Flow<Boolean> = flow {
        handleApiResultNoTransformation(
            apiResult = apiClient.sendFriendRequest(userId),
            ioDispatcher = ioDispatcher
        ).collect { result ->
            emit(result is RepositoryResult.Success)
        }
    }

    override suspend fun acceptFriendRequest(userId: String): Flow<Boolean> = flow {
        handleApiResultNoTransformation(
            apiResult = apiClient.acceptFriendRequest(userId),
            ioDispatcher = ioDispatcher
        ).collect { result ->
            emit(result is RepositoryResult.Success)
        }
    }

    override suspend fun deleteFriendRequest(userId: String): Flow<Boolean> = flow {
        handleApiResultNoTransformation(
            apiResult = apiClient.deleteFriendRequest(userId),
            ioDispatcher = ioDispatcher
        ).collect { result ->
            emit(result is RepositoryResult.Success)
        }
    }
}
