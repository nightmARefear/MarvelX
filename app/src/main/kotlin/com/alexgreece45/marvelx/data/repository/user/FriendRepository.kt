package com.alexgreece45.marvelx.data.repository.user

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.user.friend.ResponseFriend
import com.alexgreece45.marvelx.model.model.user.UserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface FriendRepository {

    /**
     * Retrieves the friends of the given user ID.
     */
    suspend fun getFriends(userId: UserId): Flow<RepositoryResult<List<ResponseFriend>>>

    /**
     * Retrieves the friends of the current user.
     */
    suspend fun getCurrentUserFriends(): Flow<RepositoryResult<List<ResponseFriend>>>

    /**
     * Deletes the friend with the given ID.
     */
    suspend fun deleteFriend(userId: UserId): Boolean
}

@Single
internal class FriendRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : FriendRepository {

    override suspend fun getFriends(userId: UserId): Flow<RepositoryResult<List<ResponseFriend>>> =
        flow {
            emitAll(
                handleApiResultNoTransformationWithData(
                    apiCall = { apiClient.getUserFriends(userId.value) },
                    ioDispatcher = ioDispatcher
                )
            )
        }

    override suspend fun getCurrentUserFriends(): Flow<RepositoryResult<List<ResponseFriend>>> =
        flow {
            emitAll(
                handleApiResultNoTransformationWithData(
                    apiCall = apiClient::getFriends,
                    ioDispatcher = ioDispatcher
                )
            )
        }

    override suspend fun deleteFriend(userId: UserId): Boolean {
        return handleApiResultNoTransformationNoFlow(
            apiCall = { apiClient.deleteFriend(userId.value) },
            ioDispatcher = ioDispatcher
        ) is RepositoryResult.Success
    }
}