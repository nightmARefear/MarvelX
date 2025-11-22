package com.alexgreece45.marvelx.domain.interactors.user

import com.alexgreece45.marvelx.data.repository.user.FriendRequestRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResult
import com.alexgreece45.marvelx.domain.model_api.user.friendRequest.DeleteFriendRequestType
import com.alexgreece45.marvelx.domain.model_api.user.friendRequest.IncomingFriendRequestDomain
import com.alexgreece45.marvelx.domain.model_api.user.friendRequest.toIncomingFriendRequestList
import com.alexgreece45.marvelx.network_logic.models_api.user.incomingFriendRequest.ResponseIncomingFriendRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

interface FriendRequestInteractor {

    /**
     * Retrieves all the incoming friend requests of the currently logged in user.
     */
    suspend fun getIncomingFriendRequests(): Flow<InteractorPartialState<List<IncomingFriendRequestDomain>>>
    suspend fun sendFriendRequest(userId: String): Flow<Boolean>
    suspend fun acceptFriendRequest(userId: String): Flow<Boolean>
    suspend fun deleteFriendRequest(
        userId: String,
        deleteFriendRequestType: DeleteFriendRequestType
    ): Flow<Boolean>
}

class FriendRequestInteractorImpl(
    private val repository: FriendRequestRepository
) : FriendRequestInteractor {

    override suspend fun getIncomingFriendRequests(): Flow<InteractorPartialState<List<IncomingFriendRequestDomain>>> =
        flow {
            repository.getIncomingFriendRequests().collect { repositoryResult ->
                emitAll(
                    handleRepositoryResult(
                        repositoryResult = repositoryResult,
                        transformation = List<ResponseIncomingFriendRequest>::toIncomingFriendRequestList
                    )
                )
            }
        }

    override suspend fun sendFriendRequest(userId: String): Flow<Boolean> =
        repository.sendFriendRequest(userId)

    override suspend fun acceptFriendRequest(userId: String): Flow<Boolean> =
        repository.acceptFriendRequest(userId)

    override suspend fun deleteFriendRequest(
        userId: String,
        deleteFriendRequestType: DeleteFriendRequestType
    ): Flow<Boolean> =
        repository.deleteFriendRequest(userId, deleteFriendRequestType.id)
}
