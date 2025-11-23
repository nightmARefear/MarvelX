package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.data.model.RepositoryPrivacySettings
import com.alexgreece45.marvelx.data.model.toRepositoryPrivacySettings
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.data.repository.helper.parseUserError
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.network_logic.clients.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.user.ResponseUserError
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings.ResponsePrivacySettings
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseBasicProfileDetails
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetailsError
import com.alexgreece45.marvelx.network_logic.util.extensions.toSerialized
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
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
     * Tracks whether the user is logged in via a reactive way.
     */
    suspend fun getUserLoggedInFlow(): StateFlow<Boolean>

    /**
     * Tracks whether the user is logged in.
     */
    val isUserLoggedIn: Boolean

    /**
     * Sets the user logged in value.
     */
    suspend fun setUserLoggedIn(loggedIn: Boolean)
}

class UserRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) : UserRepository {

    private var _isUserLoggedIn: MutableStateFlow<Boolean> = MutableStateFlow(false)

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
        emitAll(
            handleApiResultNoTransformation(
                apiResult = apiClient.updateCurrentUserProfileDetails(request),
                ioDispatcher = ioDispatcher
            )
        )
    }

    override suspend fun updateUserProfileImage(
        userId: String,
        imagePart: MultipartBody.Part
    ): Flow<RepositoryResult<String>> =
        flow {
            emitAll(
                handleApiResultNoTransformation(
                    apiResult = apiClient.updateUserProfileImage(
                        userId,
                        imagePart
                    ),
                    ioDispatcher = ioDispatcher
                )
            )
        }

    override suspend fun getPrivacySettings(): Flow<RepositoryResult<RepositoryPrivacySettings>> =
        flow {
            emitAll(
                handleApiResult(
                    apiResult = apiClient.getUserPrivacySettings(),
                    transformation = ResponsePrivacySettings::toRepositoryPrivacySettings,
                    ioDispatcher = ioDispatcher
                )
            )
        }

    override suspend fun updatePrivacySettings(requestPrivacySettings: RequestPrivacySettings): Flow<RepositoryResult<String>> =
        flow {
            emitAll(
                handleApiResultNoTransformation(
                    apiResult = apiClient.updateUserPrivacySettings(
                        requestPrivacySettings
                    ),
                    ioDispatcher = ioDispatcher
                )
            )
        }

    override suspend fun getUserLoggedInFlow(): StateFlow<Boolean> = _isUserLoggedIn

    override val isUserLoggedIn: Boolean
        get() = _isUserLoggedIn.value

    override suspend fun setUserLoggedIn(loggedIn: Boolean) {
        _isUserLoggedIn.value = loggedIn
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
