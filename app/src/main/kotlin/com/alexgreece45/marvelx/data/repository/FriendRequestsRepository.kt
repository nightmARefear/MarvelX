package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.network_logic.api.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.user.BodyUserId
import com.alexgreece45.marvelx.network_logic.models_generic.ApiResult
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.CoroutineContext

interface FriendRequestsRepository {
    suspend fun sendFriendRequest(userId: String): Flow<Boolean>
    suspend fun acceptFriendRequest(userId: String): Flow<Boolean>
    suspend fun deleteFriendRequest(userId: String): Flow<Boolean>
}

class FriendRequestsRepositoryImpl(
    private val apiClient: ApiClient,
    private val ioDispatcher: CoroutineContext
) : FriendRequestsRepository {

    override suspend fun sendFriendRequest(userId: String): Flow<Boolean> = flow {
        when (apiClient.sendFriendRequest(userId)) {
            is ApiResult.Error,
            is ApiResult.Failed,
            ApiResult.NetworkError -> {
                emit(false)
            }

            is ApiResult.Success -> emit(true)
        }
    }.safeAsync(ioDispatcher) {
        false
    }

    override suspend fun acceptFriendRequest(userId: String): Flow<Boolean> = flow {
        when (apiClient.acceptFriendRequest(userId)) {
            is ApiResult.Error,
            is ApiResult.Failed,
            ApiResult.NetworkError -> {
                emit(false)
            }

            is ApiResult.Success -> emit(true)
        }
    }.safeAsync(ioDispatcher) {
        false
    }

    override suspend fun deleteFriendRequest(userId: String): Flow<Boolean> = flow {
        when (apiClient.deleteFriendRequest(userId)) {
            is ApiResult.Error,
            is ApiResult.Failed,
            ApiResult.NetworkError -> {
                emit(false)
            }

            is ApiResult.Success -> emit(true)
        }
    }.safeAsync(ioDispatcher) {
        false
    }
}
