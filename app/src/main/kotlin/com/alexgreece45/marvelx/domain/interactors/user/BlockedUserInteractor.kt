package com.alexgreece45.marvelx.domain.interactors.user

import com.alexgreece45.marvelx.core.common.extensions.safeAsync
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.core.network.model.api.user.block.ResponseBlockedUser
import com.alexgreece45.marvelx.data.repository.user.BlockUserRepositoryResult
import com.alexgreece45.marvelx.data.repository.user.BlockedUserRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResult
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResultNoTransformation
import com.alexgreece45.marvelx.domain.model_api.user.block.BlockedUserDomain
import com.alexgreece45.marvelx.domain.model_api.user.block.UserBlockResultType
import com.alexgreece45.marvelx.domain.model_api.user.block.toBlockedUserDomainList
import com.alexgreece45.marvelx.domain.model_api.user.block.toUserBlockResultDomain
import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import okio.IOException
import org.koin.core.annotation.Factory

interface BlockedUserInteractor {

    /**
     * Returns the blocked users.
     */
    suspend fun getBlockedUsers(): Flow<InteractorPartialState<List<BlockedUserDomain>>>

    /**
     * Blocks the user with the given [userId].
     */
    suspend fun blockUser(userId: UserId): Flow<BlockUserPartialState>

    /**
     * Blocks the user with the given [userId].
     */
    suspend fun unblockUser(userId: UserId): Flow<InteractorPartialState<String>>
}

@Factory
internal class BlockedUserInteractorImpl(
    private val logger: AppLogger,
    private val resourceProvider: ResourceProvider,
    private val repository: BlockedUserRepository,
    private val userSessionRepository: UserSessionRepository
) : BlockedUserInteractor {

    override suspend fun getBlockedUsers(): Flow<InteractorPartialState<List<BlockedUserDomain>>> =
        flow {
            repository.getBlockedUsers().collect { repositoryResult ->
                emitAll(
                    handleRepositoryResult(
                        repositoryResult = repositoryResult,
                        transformation = List<ResponseBlockedUser>::toBlockedUserDomainList
                    )
                )
            }
        }

    override suspend fun blockUser(userId: UserId): Flow<BlockUserPartialState> =
        flow {
            if (userId.value == userSessionRepository.getUserId()) {
                emit(BlockUserPartialState.Failed(UserBlockResultType.SameUser))
                return@flow
            }

            repository.blockUser(userId).collect { repositoryResult ->
                when (repositoryResult) {
                    BlockUserRepositoryResult.Success -> {
                        emit(BlockUserPartialState.Success)
                    }

                    is BlockUserRepositoryResult.Failed -> {
                        emit(BlockUserPartialState.Failed(repositoryResult.repositoryUserBlockResult.toUserBlockResultDomain().userBlockResultType))
                    }
                }
            }
        }.safeAsync {
            logger.e("BlockedUserInteractor") { "(blockUser) e: ${it.localizedMessage.orEmpty()}" }
            when (it) {
                is IOException -> {
                    BlockUserPartialState.Error(errorMessage = resourceProvider.getNoInternetConnectionMessage())
                }

                else -> {
                    BlockUserPartialState.Error(errorMessage = resourceProvider.getGenericErrorMessage())
                }
            }
        }

    override suspend fun unblockUser(userId: UserId): Flow<InteractorPartialState<String>> = flow {
        repository.unblockUser(userId).collect { repositoryResult ->
            emitAll(handleRepositoryResultNoTransformation(repositoryResult))
        }
    }
}

sealed interface BlockUserPartialState {
    data object Success : BlockUserPartialState
    data class Failed(val userBlockResultType: UserBlockResultType) : BlockUserPartialState
    data class Error(val errorMessage: String) : BlockUserPartialState
}