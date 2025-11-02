package com.alexgreece45.marvelx.data.repository.user

import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.clients.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.user.friend.ResponseFriend
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

interface FriendRepository {

    /**
     * Retrieves the friends of the given user ID.
     */
    suspend fun getFriends(userId: String): Flow<RepositoryResult<List<ResponseFriend>>>

    /**
     * Retrieves the friends of the current user.
     */
    suspend fun getCurrentUserFriends(): Flow<RepositoryResult<List<ResponseFriend>>>

    /**
     * Deletes the friend with the given ID.
     */
    suspend fun deleteFriend(userId: String): Flow<Boolean>
}

class FriendRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : FriendRepository {

    override suspend fun getFriends(userId: String): Flow<RepositoryResult<List<ResponseFriend>>> =
        flow {
            emitAll(
                handleApiResultNoTransformationWithData(
                    apiResult = apiClient.getUserFriends(userId),
                    ioDispatcher = ioDispatcher
                )
            )
        }

    override suspend fun getCurrentUserFriends(): Flow<RepositoryResult<List<ResponseFriend>>> =
        flow {
            emitAll(
                handleApiResultNoTransformationWithData(
                    apiResult = apiClient.getFriends(),
                    ioDispatcher = ioDispatcher
                )
            )
        }

    override suspend fun deleteFriend(userId: String): Flow<Boolean> = flow {
        handleApiResultNoTransformation(
            apiResult = apiClient.deleteFriend(userId),
            ioDispatcher = ioDispatcher
        ).collect { result ->
            emit(result is RepositoryResult.Success)
        }
    }
}
