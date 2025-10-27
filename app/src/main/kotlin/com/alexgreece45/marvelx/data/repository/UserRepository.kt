package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.data.model.RepositoryPrivacySettings
import com.alexgreece45.marvelx.data.model.RepositoryUserBlockResult
import com.alexgreece45.marvelx.data.model.toRepositoryPrivacySettings
import com.alexgreece45.marvelx.data.model.toRepositoryUserBlock
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformationWithData
import com.alexgreece45.marvelx.data.repository.helper.parseUserError
import com.alexgreece45.marvelx.network_logic.api.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.user.ResponseUserError
import com.alexgreece45.marvelx.network_logic.models_api.user.block.ResponseBlockedUser
import com.alexgreece45.marvelx.network_logic.models_api.user.block.ResponseUserBlockResult
import com.alexgreece45.marvelx.network_logic.models_api.user.friend.ResponseFriend
import com.alexgreece45.marvelx.network_logic.models_api.user.incomingFriendRequest.ResponseIncomingFriendRequest
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.ResponsePrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseBasicProfileDetails
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetailsError
import com.alexgreece45.marvelx.network_logic.util.extensions.toSerialized
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import kotlin.coroutines.CoroutineContext

interface UserRepository {

    /**
     * Gets the profile details of the given [userId].
     */
    suspend fun getProfileDetails(userId: String): GetProfileDetailsRepositoryResponse

    /**
     * Get the profile details of the current user.
     */
    suspend fun getCurrentUserProfileDetails(): Flow<GetCurrentUserProfileDetailsRepositoryResponse>

    /**
     * Updates the profile details of the current user.
     */
    suspend fun updateCurrentUserProfileDetails(
        firstName: String,
        lastName: String,
        gender: String,
        email: String
    ): Flow<RepositoryResult<String>>

    /**
     * Updates the profile image of the current user.
     *
     * @param imagePart The image data to be set as the new profile image, encapsulated in a
     * [MultipartBody.Part].
     */
    suspend fun updateUserProfileImage(
        userId: String,
        imagePart: MultipartBody.Part
    ): Flow<RepositoryResult<String>>

    /**
     * Gets the privacy settings of the current user.
     */
    suspend fun getPrivacySettings(): Flow<RepositoryResult<RepositoryPrivacySettings>>

    /**
     * Updates the privacy settings of the current user.
     */
    suspend fun updatePrivacySettings(requestPrivacySettings: RequestPrivacySettings): Flow<RepositoryResult<String>>

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
    suspend fun deleteFriend(userId: String): Flow<RepositoryResult<String>>

    /**
     * Returns the incoming friend requests of the current user.
     */
    suspend fun getIncomingFriendRequests(): Flow<RepositoryResult<List<ResponseIncomingFriendRequest>>>

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

class UserRepositoryImpl(
    private val apiClient: ApiClient,
    private val ioDispatcher: CoroutineContext
) : UserRepository {

    override suspend fun getProfileDetails(
        userId: String
    ): GetProfileDetailsRepositoryResponse =
        apiClient.getUserProfileDetails(userId)
            .let { response ->
                if (response.isSuccessful) {
                    GetProfileDetailsRepositoryResponse.Success(
                        response = response.body()
                    )

                } else {
                    GetProfileDetailsRepositoryResponse.Failed(
                        responseProfileDetailsError = response.errorBody()
                            .toSerialized<ResponseProfileDetailsError>()
                    )
                }
            }

    override suspend fun getCurrentUserProfileDetails(): Flow<GetCurrentUserProfileDetailsRepositoryResponse> =
        flow {
            apiClient.getCurrentUserProfileDetails().let { response ->
                if (response.isSuccessful) {
                    emit(GetCurrentUserProfileDetailsRepositoryResponse.Success(response.body()))
                } else {
                    emit(
                        GetCurrentUserProfileDetailsRepositoryResponse.Failed(
                            responseUserError = parseUserError(
                                response
                            )
                        )
                    )
                }
            }
        }

    override suspend fun updateCurrentUserProfileDetails(
        firstName: String,
        lastName: String,
        gender: String,
        email: String
    ): Flow<RepositoryResult<String>> = flow {
        val request = RequestProfileDetailsUpdate(
            firstName = firstName,
            lastName = lastName,
            gender = gender,
            email = email
        )
        handleApiResultNoTransformation(apiClient.updateCurrentUserProfileDetails(request)).collect {
            emit(it)
        }
    }

    override suspend fun updateUserProfileImage(
        userId: String,
        imagePart: MultipartBody.Part
    ): Flow<RepositoryResult<String>> =
        flow {
            handleApiResultNoTransformation(apiClient.updateUserProfileImage(userId, imagePart)).collect {
                emit(it)
            }
        }

    override suspend fun getPrivacySettings(): Flow<RepositoryResult<RepositoryPrivacySettings>> =
        flow {
            handleApiResult(
                apiClient.getUserPrivacySettings(),
                ResponsePrivacySettings::toRepositoryPrivacySettings
            ).collect {
                emit(it)
            }
        }

    override suspend fun updatePrivacySettings(requestPrivacySettings: RequestPrivacySettings): Flow<RepositoryResult<String>> =
        flow {
            handleApiResultNoTransformation(
                apiClient.updateUserPrivacySettings(
                    requestPrivacySettings
                )
            ).collect {
                emit(it)
            }
        }

    override suspend fun getFriends(userId: String): Flow<RepositoryResult<List<ResponseFriend>>> =
        flow {
            handleApiResultNoTransformationWithData(
                apiResult = apiClient.getUserFriends(userId),
                ioDispatcher = ioDispatcher
            ).collect { emit(it) }
        }

    override suspend fun getCurrentUserFriends(): Flow<RepositoryResult<List<ResponseFriend>>> =
        flow {
            handleApiResultNoTransformationWithData(
                apiResult = apiClient.getFriends(),
                ioDispatcher = ioDispatcher
            ).collect { emit(it) }
        }

    override suspend fun deleteFriend(userId: String): Flow<RepositoryResult<String>> = flow {
        handleApiResultNoTransformation(
            apiClient.deleteFriend(userId)
        ).collect { emit(it) }
    }

    override suspend fun getIncomingFriendRequests(): Flow<RepositoryResult<List<ResponseIncomingFriendRequest>>> =
        flow {
            handleApiResultNoTransformationWithData(
                apiResult = apiClient.getIncomingFriendRequests(),
                ioDispatcher = ioDispatcher
            ).collect { emit(it) }
        }

    override suspend fun getBlockedUsers(): Flow<RepositoryResult<List<ResponseBlockedUser>>> =
        flow {
            handleApiResultNoTransformationWithData(
                apiResult = apiClient.getBlockedUsers(),
                ioDispatcher = ioDispatcher
            ).collect { emit(it) }
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
        handleApiResultNoTransformation(
            apiClient.unblockUser(userId)
        ).collect { emit(it) }
    }
}

sealed class GetCurrentUserProfileDetailsRepositoryResponse {
    data class Success(val response: ResponseBasicProfileDetails?) :
        GetCurrentUserProfileDetailsRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        GetCurrentUserProfileDetailsRepositoryResponse()
}

sealed class GetProfileDetailsRepositoryResponse {
    data class Success(val response: ResponseProfileDetails?) :
        GetProfileDetailsRepositoryResponse()

    data class Failed(val responseProfileDetailsError: ResponseProfileDetailsError) :
        GetProfileDetailsRepositoryResponse()
}

sealed interface BlockUserRepositoryResult {
    data object Success : BlockUserRepositoryResult
    data class Failed(val repositoryUserBlockResult: RepositoryUserBlockResult) :
        BlockUserRepositoryResult
}
