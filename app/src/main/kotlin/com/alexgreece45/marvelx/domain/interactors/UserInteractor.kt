package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.data.model.RepositoryPrivacySettings
import com.alexgreece45.marvelx.data.repository.GetCurrentUserProfileDetailsRepositoryResponse
import com.alexgreece45.marvelx.data.repository.GetProfileDetailsRepositoryResponse
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResult
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResultNoTransformation
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.PrivacySettingsDomain
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.toPrivacySettingsDomain
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.toRequestPrivacySettings
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.BasicProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsErrorType
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.toBasicProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.toProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.toProfileDetailsErrorDomain
import com.alexgreece45.marvelx.domain.model_generic.ReadFileContentData
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.getFileNameExtension
import com.alexgreece45.marvelx.util.extensions.getMediaTypeFromFileName
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.IOException

interface UserInteractor {

    /**
     * Gets the profile details of the given [userId].
     */
    suspend fun getProfileDetails(userId: String): Flow<GetProfileDetailsPartialState>

    /**
     * Gets the basic profile details of the current user.
     */
    suspend fun getCurrentUserBasicProfileDetails(): Flow<GetCurrentUserProfileDetailsPartialState>

    /**
     * Updates the profile details of the current user.
     */
    suspend fun updateCurrentUserProfileDetails(profileDetails: BasicProfileDetailsDomain): Flow<InteractorPartialState<String>>

    /**
     * Updates the profile image of the current user.
     *
     * @param profileImageReadFileContentData The profile image content data.
     */
    suspend fun updateUserProfileImage(profileImageReadFileContentData: ReadFileContentData): Flow<InteractorPartialState<String>>

    /**
     * Gets the privacy settings of the current user.
     */
    suspend fun getPrivacySettings(): Flow<InteractorPartialState<PrivacySettingsDomain>>

    /**
     * Updates the privacy settings of the current user.
     */
    suspend fun updatePrivacySettings(privacySettingsDomain: PrivacySettingsDomain): Flow<InteractorPartialState<String>>
}

class UserInteractorImpl(
    private val logger: AppLogger,
    private val resourceProvider: ResourceProvider,
    private val repository: UserRepository,
    private val prefKeys: PrefKeys
) : UserInteractor {

    override suspend fun getProfileDetails(userId: String): Flow<GetProfileDetailsPartialState> =
        flow {
            when (val profileDetailsResponse = repository.getProfileDetails(userId)) {
                is GetProfileDetailsRepositoryResponse.Failed -> {
                    emit(
                        GetProfileDetailsPartialState.Failed(
                            profileDetailsErrorType = profileDetailsResponse.responseProfileDetailsError.toProfileDetailsErrorDomain().profileDetailsErrorType
                        )
                    )
                }

                is GetProfileDetailsRepositoryResponse.Success -> {
                    profileDetailsResponse.response?.let { responseProfileDetails ->
                        emit(GetProfileDetailsPartialState.Success(profileDetailsDomain = responseProfileDetails.toProfileDetailsDomain()))
                    }
                }
            }
        }.safeAsync {
            logger.e("UserInteractor") { "(getProfileDetails) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> GetProfileDetailsPartialState.NoInternetConnection

                else -> GetProfileDetailsPartialState.Error(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }

    override suspend fun getCurrentUserBasicProfileDetails(): Flow<GetCurrentUserProfileDetailsPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            repository.getCurrentUserProfileDetails().collect {
                when (it) {
                    is GetCurrentUserProfileDetailsRepositoryResponse.Success -> {
                        when (val response = it.response) {
                            null -> {
                                emit(
                                    GetCurrentUserProfileDetailsPartialState.Failed(
                                        genericErrorMessage
                                    )
                                )
                            }

                            else -> {
                                emit(
                                    GetCurrentUserProfileDetailsPartialState.Success(
                                        response.toBasicProfileDetailsDomain(
                                            isGenderVisible = true
                                        )
                                    )
                                )
                            }
                        }
                    }

                    is GetCurrentUserProfileDetailsRepositoryResponse.Failed -> {
                        emit(GetCurrentUserProfileDetailsPartialState.Failed(genericErrorMessage))
                    }
                }
            }
        }.safeAsync {
            logger.e("UserInteractor") { "(getCurrentUserProfileDetails) error: ${it.localizedMessage.orEmpty()}" }
            when (it) {
                is IOException -> GetCurrentUserProfileDetailsPartialState.NoInternetConnection
                else -> GetCurrentUserProfileDetailsPartialState.Failed(resourceProvider.getGenericErrorMessage())
            }
        }

    override suspend fun updateCurrentUserProfileDetails(profileDetails: BasicProfileDetailsDomain): Flow<InteractorPartialState<String>> =
        flow {
            repository.updateCurrentUserProfileDetails(
                firstName = profileDetails.firstName,
                lastName = profileDetails.lastName,
                gender = profileDetails.gender.key,
                email = profileDetails.email
            ).collect { repositoryResult ->
                emitAll(handleRepositoryResultNoTransformation(repositoryResult))
            }
        }

    override suspend fun updateUserProfileImage(profileImageReadFileContentData: ReadFileContentData): Flow<InteractorPartialState<String>> =
        flow {
            val imageRequestBody = profileImageReadFileContentData.fileContent.toRequestBody(
                profileImageReadFileContentData.fileName.getMediaTypeFromFileName()
            )

            val imagePart = MultipartBody.Part.createFormData(
                name = "profile_image",
                filename = "profile_image." + profileImageReadFileContentData.fileName.getFileNameExtension(),
                body = imageRequestBody
            )
            repository.updateUserProfileImage(prefKeys.getUserId(), imagePart)
                .collect { repositoryResult ->
                    emitAll(handleRepositoryResultNoTransformation(repositoryResult))
                }
        }.catch {
            logger.e("UserInteractor") { "(updateUserProfileImage) e: ${it.localizedMessage.orEmpty()}" }
            when (it) {
                is IllegalArgumentException -> InteractorPartialState.Error<String>(it.localizedMessage.orEmpty()) // Can be thrown from `.toRequestBody`.
                else -> InteractorPartialState.Error<String>(resourceProvider.getGenericErrorMessage())
            }
        }

    override suspend fun getPrivacySettings(): Flow<InteractorPartialState<PrivacySettingsDomain>> =
        flow {
            repository.getPrivacySettings().collect { repositoryResult ->
                emitAll(
                    handleRepositoryResult(
                        repositoryResult,
                        RepositoryPrivacySettings::toPrivacySettingsDomain
                    )
                )
            }
        }

    override suspend fun updatePrivacySettings(privacySettingsDomain: PrivacySettingsDomain): Flow<InteractorPartialState<String>> =
        flow {
            repository.updatePrivacySettings(privacySettingsDomain.toRequestPrivacySettings())
                .collect { repositoryResult ->
                    handleRepositoryResultNoTransformation(repositoryResult).collect {
                        emit(it)
                    }
                }
        }
}

sealed class GetCurrentUserProfileDetailsPartialState {
    data class Success(val basicProfileDetails: BasicProfileDetailsDomain) :
        GetCurrentUserProfileDetailsPartialState()

    data class Failed(val errorMessage: String) : GetCurrentUserProfileDetailsPartialState()

    data object NoInternetConnection : GetCurrentUserProfileDetailsPartialState()
}

sealed class GetProfileDetailsPartialState {
    data class Success(val profileDetailsDomain: ProfileDetailsDomain) :
        GetProfileDetailsPartialState()

    data class Failed(val profileDetailsErrorType: ProfileDetailsErrorType) :
        GetProfileDetailsPartialState()

    data class Error(val errorMessage: String) : GetProfileDetailsPartialState()

    data object NoInternetConnection : GetProfileDetailsPartialState()
}
