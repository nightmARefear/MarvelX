package com.alexgreece45.marvelx.data.repository.user

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.user.block.ResponseBlockedUser
import com.alexgreece45.marvelx.core.network.model.api.user.block.ResponseUserBlockResult
import com.alexgreece45.marvelx.data.model.RepositoryUserBlockResult
import com.alexgreece45.marvelx.data.model.toRepositoryUserBlock
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
    suspend fun blockUser(userId: String): Flow<BlockUserRepositoryResult>

    /**
     * Unblocks the user with [userId].
     */
    suspend fun unblockUser(userId: String): Flow<RepositoryResult<String>>
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
                    apiResult = apiClient.getBlockedUsers(),
                    ioDispatcher = ioDispatcher
                )
            )
        }

    override suspend fun blockUser(userId: String): Flow<BlockUserRepositoryResult> = flow {
        apiClient.blockUser(userId).let { response ->
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

    override suspend fun unblockUser(userId: String): Flow<RepositoryResult<String>> = flow {
        emitAll(
            handleApiResultNoTransformation(
                apiResult = apiClient.unblockUser(userId),
                ioDispatcher = ioDispatcher
            )
        )
    }
}

sealed interface BlockUserRepositoryResult {
    data object Success : BlockUserRepositoryResult
    data class Failed(val repositoryUserBlockResult: RepositoryUserBlockResult) :
        BlockUserRepositoryResult
}