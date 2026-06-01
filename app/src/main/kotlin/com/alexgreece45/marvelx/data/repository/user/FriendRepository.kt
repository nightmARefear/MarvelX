package com.alexgreece45.marvelx.data.repository.user

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface FriendRepository {

    /**
     * Retrieves the friends of the given user ID.
     */
    suspend fun getFriends(userId: String): Flow<RepositoryResult<List<com.alexgreece45.marvelx.core.network.model.api.user.friend.ResponseFriend>>>

    /**
     * Retrieves the friends of the current user.
     */
    suspend fun getCurrentUserFriends(): Flow<RepositoryResult<List<com.alexgreece45.marvelx.core.network.model.api.user.friend.ResponseFriend>>>

    /**
     * Deletes the friend with the given ID.
     */
    suspend fun deleteFriend(userId: String): Flow<Boolean>
}

@Single
class FriendRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : FriendRepository {

    override suspend fun getFriends(userId: String): Flow<RepositoryResult<List<com.alexgreece45.marvelx.core.network.model.api.user.friend.ResponseFriend>>> =
        flow {
            emitAll(
                handleApiResultNoTransformationWithData(
                    apiResult = apiClient.getUserFriends(userId),
                    ioDispatcher = ioDispatcher
                )
            )
        }

    override suspend fun getCurrentUserFriends(): Flow<RepositoryResult<List<com.alexgreece45.marvelx.core.network.model.api.user.friend.ResponseFriend>>> =
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
