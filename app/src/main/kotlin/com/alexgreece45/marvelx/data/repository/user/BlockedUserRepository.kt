package com.alexgreece45.marvelx.data.repository.user

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.user.block.ResponseBlockedUser
import com.alexgreece45.marvelx.core.network.model.api.user.block.ResponseUserBlockResult
import com.alexgreece45.marvelx.data.model.RepositoryUserBlockResult
import com.alexgreece45.marvelx.data.model.toRepositoryUserBlock
import com.alexgreece45.marvelx.model.model.user.UserId
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface BlockedUserRepository {

    /**
     * Returns the blocked users of the current user.
     */
    suspend fun getBlockedUsers(): Flow<RepositoryResult<List<ResponseBlockedUser>>>

    /**
     * Blocks the user with [userId].
     */
    suspend fun blockUser(userId: UserId): Flow<BlockUserRepositoryResult>

    /**
     * Unblocks the user with [userId].
     */
    suspend fun unblockUser(userId: UserId): RepositoryResult<String>
}

@Single
class BlockedUserRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : BlockedUserRepository {

    override suspend fun getBlockedUsers(): Flow<RepositoryResult<List<ResponseBlockedUser>>> =
        flow {
            emitAll(
                handleApiResultNoTransformationWithData(
                    apiCall = apiClient::getBlockedUsers,
                    ioDispatcher = ioDispatcher
                )
            )
        }

    override suspend fun blockUser(userId: UserId): Flow<BlockUserRepositoryResult> = flow {
        apiClient.blockUser(userId.value).let { response ->
            if (response.isSuccessful) {
                emit(BlockUserRepositoryResult.Success)
            } else {
                emit(
                    BlockUserRepositoryResult.Failed(
                        repositoryUserBlockResult =
                            Gson().fromJson(
                                response.errorBody()?.byteString()?.utf8(),
                                ResponseUserBlockResult::class.java
                            ).toRepositoryUserBlock()
                    )
                )
            }
        }
    }

    override suspend fun unblockUser(userId: UserId): RepositoryResult<String> {
        return handleApiResultNoTransformationNoFlow(
            apiCall = { apiClient.unblockUser(userId.value) },
            ioDispatcher = ioDispatcher
        )
    }
}

sealed interface BlockUserRepositoryResult {
    data object Success : BlockUserRepositoryResult
    data class Failed(val repositoryUserBlockResult: RepositoryUserBlockResult) :
        BlockUserRepositoryResult
}