package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.ResponsePrivacySettings
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.core.network.util.extensions.toSerialized
import com.alexgreece45.marvelx.data.model.RepositoryPrivacySettings
import com.alexgreece45.marvelx.data.model.profileDetails.RepositoryBasicProfileDetails
import com.alexgreece45.marvelx.data.model.profileDetails.toData
import com.alexgreece45.marvelx.data.model.toRepositoryPrivacySettings
import com.alexgreece45.marvelx.data.repository.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResult
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.data.repository.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.data.repository.helper.parseUserError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface UserRepository {

    /**
     * Gets the profile details of the given [userId].
     */
    suspend fun getProfileDetails(userId: String): GetProfileDetailsRepositoryResponse

    suspend fun getBasicProfileDetails(userId: String): RepositoryResult<RepositoryBasicProfileDetails>

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
    ): RepositoryResult<String>

    /**
     * Gets the privacy settings of the current user.
     */
    suspend fun getPrivacySettings(): Flow<RepositoryResult<RepositoryPrivacySettings>>

    /**
     * Updates the privacy settings of the current user.
     */
    suspend fun updatePrivacySettings(requestPrivacySettings: com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.RequestPrivacySettings): Flow<RepositoryResult<String>>

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

@Single
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
                            .toSerialized<com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseProfileDetailsError>()
                    )
                }
            }

    override suspend fun getBasicProfileDetails(userId: String): RepositoryResult<RepositoryBasicProfileDetails> {
        return handleApiResult(
            apiResult = apiClient.getBasicProfileDetails(userId),
            transformation = { this.toData() },
            ioDispatcher = ioDispatcher
        ).first()
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
        val request =
            RequestProfileDetailsUpdate(
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
    ): RepositoryResult<String> {
        return handleApiResultNoTransformationNoFlow(
            apiResult = apiClient.updateUserProfileImage(
                userId,
                imagePart
            ),
            ioDispatcher = ioDispatcher
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
    data class Success(val response: com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseBasicProfileDetails?) :
        GetCurrentUserProfileDetailsRepositoryResponse()

    data class Failed(val responseUserError: com.alexgreece45.marvelx.core.network.model.api.user.ResponseUserError) :
        GetCurrentUserProfileDetailsRepositoryResponse()
}

sealed class GetProfileDetailsRepositoryResponse {
    data class Success(val response: com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseProfileDetails?) :
        GetProfileDetailsRepositoryResponse()

    data class Failed(val responseProfileDetailsError: com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseProfileDetailsError) :
        GetProfileDetailsRepositoryResponse()
}
