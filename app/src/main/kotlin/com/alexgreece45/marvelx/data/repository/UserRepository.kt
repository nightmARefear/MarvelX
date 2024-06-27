package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.data.model.RepositoryPrivacySettings
import com.alexgreece45.marvelx.data.model.RepositoryUserBlock
import com.alexgreece45.marvelx.data.model.RepositoryUsersBlocked
import com.alexgreece45.marvelx.data.model.toRepositoryPrivacySettings
import com.alexgreece45.marvelx.data.model.toRepositoryUserBlock
import com.alexgreece45.marvelx.data.model.toRepositoryUsersBlocked
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.data.repository.helper.parseUserError
import com.alexgreece45.marvelx.network_logic.api.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.user.ResponseUserError
import com.alexgreece45.marvelx.network_logic.models_api.user.ResponseUsersBlocked
import com.alexgreece45.marvelx.network_logic.models_api.user.block.ResponseUserBlock
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.ResponsePrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseBasicProfileDetails
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetailsError
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody

interface UserRepository {
    /**
     * Gets the profile details of the given [userId].
     */
    suspend fun getProfileDetails(userId: Int): Flow<GetProfileDetailsRepositoryResponse>

    /**
     * Get the profile details of the current user.
     */
    suspend fun getCurrentUserProfileDetails(): Flow<GetCurrentUserProfileDetailsRepositoryResponse>

    suspend fun getCurrentUserBlockedUsers(): Flow<RepositoryResult<RepositoryUsersBlocked>>

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
    suspend fun updateUserProfileImage(imagePart: MultipartBody.Part): Flow<RepositoryResult<String>>

    /**
     * Gets the privacy settings of the current user.
     */
    suspend fun getPrivacySettings(): Flow<RepositoryResult<RepositoryPrivacySettings>>

    /**
     * Updates the privacy settings of the current user.
     */
    suspend fun updatePrivacySettings(requestPrivacySettings: RequestPrivacySettings): Flow<RepositoryResult<String>>

    /**
     * Blocks the user with [userId].
     */
    suspend fun blockUser(userId: Int): Flow<BlockUserRepositoryResult>

    /**
     * Unblocks the user with [userId].
     */
    suspend fun unblockUser(userId: Int): Flow<RepositoryResult<String>>
}

class UserRepositoryImpl(
    private val apiClient: ApiClient
) : UserRepository {
    override suspend fun getProfileDetails(
        userId: Int
    ): Flow<GetProfileDetailsRepositoryResponse> =
        flow {
            apiClient.getUserProfileDetails(userId)
                .let { response ->
                    if (response.isSuccessful) {
                        emit(
                            GetProfileDetailsRepositoryResponse.Success(
                                response = response.body()
                            )
                        )
                    } else {
                        emit(
                            GetProfileDetailsRepositoryResponse.Failed(
                                responseProfileDetailsError = Gson().fromJson(
                                    response.errorBody()?.byteString()?.utf8(),
                                    ResponseProfileDetailsError::class.java
                                )
                            )
                        )
                    }
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

    override suspend fun getCurrentUserBlockedUsers(): Flow<RepositoryResult<RepositoryUsersBlocked>> =
        flow {
            handleApiResult(
                apiResult = apiClient.getCurrentUserBlockedUsers(),
                transformation = ResponseUsersBlocked::toRepositoryUsersBlocked
            ).collect { emit(it) }
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

    override suspend fun updateUserProfileImage(imagePart: MultipartBody.Part): Flow<RepositoryResult<String>> =
        flow {
            handleApiResultNoTransformation(apiClient.updateUserProfileImage(imagePart)).collect {
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

    override suspend fun blockUser(userId: Int): Flow<BlockUserRepositoryResult> = flow {
        apiClient.blockUser(userId).let { response ->
            if (response.isSuccessful) {
                emit(BlockUserRepositoryResult.Success)
            } else {
                emit(
                    BlockUserRepositoryResult.Failed(
                        repositoryUserBlock =
                        Gson().fromJson(
                            response.errorBody()?.byteString()?.utf8(),
                            ResponseUserBlock::class.java
                        ).toRepositoryUserBlock()
                    )
                )
            }
        }
    }

    override suspend fun unblockUser(userId: Int): Flow<RepositoryResult<String>> = flow {
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
    data class Failed(val repositoryUserBlock: RepositoryUserBlock) : BlockUserRepositoryResult
}
