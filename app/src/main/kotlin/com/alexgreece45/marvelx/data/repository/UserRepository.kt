package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.extensions.LOGGER_TAG
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformation
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.MultipartBuilder
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartBuilderResult
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartItem.Image
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.MultipartResult
import com.alexgreece45.marvelx.core.data.helper.multi_part_builder.model.toMultipartResult
import com.alexgreece45.marvelx.model.model.user.UserId
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.user.ResponseUserError
import com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.RequestPrivacySettings
import com.alexgreece45.marvelx.core.network.model.api.user.privacySettings.ResponsePrivacySettings
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseBasicProfileDetails
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.core.network.model.api.user.profileDetails.ResponseProfileDetailsError
import com.alexgreece45.marvelx.core.network.util.extensions.toSerialized
import com.alexgreece45.marvelx.data.model.RepositoryPrivacySettings
import com.alexgreece45.marvelx.data.model.profileDetails.RepositoryBasicProfileDetails
import com.alexgreece45.marvelx.data.model.profileDetails.toData
import com.alexgreece45.marvelx.data.model.toRepositoryPrivacySettings
import com.alexgreece45.marvelx.data.repository.helper.parseUserError
import com.alexgreece45.marvelx.model.model.generic.AttachedFileContentData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface UserRepository {

    /**
     * Gets the profile details of the given [userId].
     */
    suspend fun getProfileDetails(userId: String): GetProfileDetailsRepositoryResponse

    suspend fun getBasicProfileDetails(userId: UserId): RepositoryResult<RepositoryBasicProfileDetails>

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
     * @param profileImage The image data to be set as the new profile image.
     */
    suspend fun updateUserProfileImage(
        userId: String,
        profileImage: AttachedFileContentData
    ): UpdateProfileImageResult

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

@Single
internal class UserRepositoryImpl(
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
    private val multipartBuilder: MultipartBuilder,
    private val appLogger: AppLogger
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

    override suspend fun getBasicProfileDetails(userId: UserId): RepositoryResult<RepositoryBasicProfileDetails> {
        return handleApiResult(
            apiResult = apiClient.getBasicProfileDetails(userId.value),
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
        profileImage: AttachedFileContentData
    ): UpdateProfileImageResult {
        return when (
            val multipartBuilderResult = multipartBuilder.constructMultipartPart(
                Image(
                    partName = "profile_image",
                    fileName = "profile_image",
                    imageFileData = profileImage
                )
            )
        ) {
            is MultipartBuilderResult.Success -> {
                handleApiResultNoTransformationNoFlow(
                    apiResult = apiClient.updateUserProfileImage(
                        userId = userId,
                        profileImage = multipartBuilderResult.data
                    ),
                    ioDispatcher = ioDispatcher,
                    logError = { errorMessage -> appLogger.e(LOGGER_TAG) { errorMessage } }
                ).toMultipartResult()
            }

            is MultipartBuilderResult.GenericError -> {
                MultipartResult.GenericError(multipartBuilderResult.errorMessage)
            }

            MultipartBuilderResult.ImageProcessingError -> {
                MultipartResult.ImageProcessingError
            }
        }
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

typealias UpdateProfileImageResult = MultipartResult<String>