package com.alexgreece45.marvelx.domain.interactors.user

import com.alexgreece45.marvelx.core.network.model.api.user.friend.ResponseFriend
import com.alexgreece45.marvelx.data.repository.user.FriendRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResult
import com.alexgreece45.marvelx.domain.model_api.user.friend.FriendDomain
import com.alexgreece45.marvelx.domain.model_api.user.friend.toFriendDomainList
import com.alexgreece45.marvelx.model.model.user.UserId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

interface FriendInteractor {

    /**
     * Retrieves all the friends of the given user ID.
     */
    suspend fun getFriends(userId: UserId): Flow<InteractorPartialState<List<FriendDomain>>>

    /**
     *  Returns the friends of the current user.
     */
    suspend fun getCurrentUserFriends(): Flow<InteractorPartialState<List<FriendDomain>>>

    /**
     * Deletes the friend with the given ID.
     */
    suspend fun deleteFriend(userId: UserId): Boolean

}

@Factory
internal class FriendInteractorImpl(
    private val repository: FriendRepository
) : FriendInteractor {

    override suspend fun getFriends(userId: UserId): Flow<InteractorPartialState<List<FriendDomain>>> =
        flow {
            repository.getFriends(userId).collect { repositoryResult ->
                emitAll(
                    handleRepositoryResult(
                        repositoryResult = repositoryResult,
                        transformation = List<ResponseFriend>::toFriendDomainList
                    )
                )
            }
        }

    override suspend fun getCurrentUserFriends(): Flow<InteractorPartialState<List<FriendDomain>>> =
        flow {
            repository.getCurrentUserFriends().collect { repositoryResult ->
                emitAll(
                    handleRepositoryResult(
                        repositoryResult = repositoryResult,
                        transformation = List<ResponseFriend>::toFriendDomainList
                    )
                )
            }
        }

    override suspend fun deleteFriend(userId: UserId): Boolean = repository.deleteFriend(userId)
}
