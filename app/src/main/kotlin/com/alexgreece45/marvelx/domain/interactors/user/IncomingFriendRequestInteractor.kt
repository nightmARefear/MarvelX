package com.alexgreece45.marvelx.domain.interactors.user

import com.alexgreece45.marvelx.data.repository.user.IncomingFriendRequestRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResult
import com.alexgreece45.marvelx.network_logic.models_api.user.incomingFriendRequest.ResponseIncomingFriendRequest
import com.alexgreece45.marvelx.ui.screens.friends.model.IncomingFriendRequestDomain
import com.alexgreece45.marvelx.ui.screens.friends.model.toIncomingFriendRequestList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow

interface IncomingFriendRequestInteractor {

    /**
     * Retrieves all the incoming friend requests of the currently logged in user.
     */
    suspend fun getIncomingFriendRequests(): Flow<InteractorPartialState<List<IncomingFriendRequestDomain>>>
    suspend fun sendFriendRequest(userId: String): Flow<Boolean>
    suspend fun acceptFriendRequest(userId: String): Flow<Boolean>
    suspend fun deleteFriendRequest(userId: String): Flow<Boolean>
}

class IncomingFriendRequestInteractorImpl(
    private val repository: IncomingFriendRequestRepository
) : IncomingFriendRequestInteractor {

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

    override suspend fun deleteFriendRequest(userId: String): Flow<Boolean> =
        repository.deleteFriendRequest(userId)
}
