package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.data.model.RepositoryPrivacySettings
import com.alexgreece45.marvelx.data.model.toRepositoryPrivacySettings
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.data.repository.helper.parseError
import com.alexgreece45.marvelx.network_logic.api.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.user.ResponseUserError
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.ResponsePrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetails
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
    suspend fun getCurrentUserProfileDetails(): Flow<GetProfileDetailsRepositoryResponse>

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
    suspend fun updatePrivacySettings(
        isGenderVisible: Boolean,
        isEmailVisible: Boolean
    ): Flow<RepositoryResult<String>>
}

class UserRepositoryImpl(
    private val apiClient: ApiClient
) : UserRepository {
    override suspend fun getProfileDetails(userId: Int): Flow<GetProfileDetailsRepositoryResponse> =
        flow {
            apiClient.getUserProfileDetails(userId).let { response ->
                if (response.isSuccessful) {
                    emit(
                        GetProfileDetailsRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        GetProfileDetailsRepositoryResponse.Failed(
                            responseUserError = parseError(
                                response
                            )
                        )
                    )
                }
            }
        }

    override suspend fun getCurrentUserProfileDetails(): Flow<GetProfileDetailsRepositoryResponse> =
        flow {
            apiClient.getUserMe().let { response ->
                if (response.isSuccessful) {
                    emit(GetProfileDetailsRepositoryResponse.Success(response.body()))
                } else {
                    emit(
                        GetProfileDetailsRepositoryResponse.Failed(
                            responseUserError = parseError(
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
        handleApiResultNoTransformation(apiClient.updateUserMe(request)).collect {
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

    override suspend fun updatePrivacySettings(
        isGenderVisible: Boolean,
        isEmailVisible: Boolean
    ): Flow<RepositoryResult<String>> = flow {
        val request = RequestPrivacySettings(isGenderVisible.toString(), isEmailVisible.toString())
        handleApiResultNoTransformation(apiClient.updateUserPrivacySettings(request)).collect {
            emit(it)
        }
    }
}

sealed class GetProfileDetailsRepositoryResponse {
    data class Success(val response: ResponseProfileDetails?) :
        GetProfileDetailsRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        GetProfileDetailsRepositoryResponse()
}
