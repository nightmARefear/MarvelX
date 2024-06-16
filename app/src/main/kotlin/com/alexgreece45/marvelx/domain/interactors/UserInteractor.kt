package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.data.model.RepositoryPrivacySettings
import com.alexgreece45.marvelx.data.repository.GetProfileDetailsRepositoryResponse
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.domain.helper.InteractorPartialState
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResult
import com.alexgreece45.marvelx.domain.helper.handleRepositoryResultNoTransformation
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.PrivacySettingsDomain
import com.alexgreece45.marvelx.domain.model_api.user.privacySettings.toPrivacySettingsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.toProfileDetailsDomain
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.fromBytesToMb
import com.alexgreece45.marvelx.util.extensions.safeAsync
import com.alexgreece45.marvelx.util.extensions.toRequestBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okio.IOException
import java.io.File

interface UserInteractor {
    /**
     * Gets the profile details of the given [userId].
     */
    suspend fun getProfileDetails(userId: Int): Flow<GetProfileDetailsPartialState>

    /**
     * Gets the profile details of the current user.
     */
    suspend fun getCurrentUserProfileDetails(): Flow<GetProfileDetailsPartialState>

    /**
     * Updates the profile details of the current user.
     */
    suspend fun updateUserProfileDetails(profileDetailsDomain: ProfileDetailsDomain): Flow<InteractorPartialState<String>>

    /**
     * Updates the profile image of the current user by using the provided image file.
     *
     * @param profileImageFile The local file containing the new profile image.
     */
    suspend fun updateUserProfileImage(profileImageFile: File): Flow<InteractorPartialState<String>>

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
    private val configLogic: ConfigLogic
) : UserInteractor {

    override suspend fun getProfileDetails(userId: Int): Flow<GetProfileDetailsPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            repository.getProfileDetails(userId).collect {
                when (it) {

                    is GetProfileDetailsRepositoryResponse.Failed -> {
                        emit(GetProfileDetailsPartialState.Failed(errorMessage = genericErrorMessage))
                    }

                    is GetProfileDetailsRepositoryResponse.Success -> {
                        it.response?.let { responseProfileDetails ->
                            emit(GetProfileDetailsPartialState.Success(profileDetailsDomain = responseProfileDetails.toProfileDetailsDomain()))
                        }
                    }
                }
            }
        }.safeAsync {
            logger.e { "(getProfileDetails) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> GetProfileDetailsPartialState.NoInternetConnection

                else -> GetProfileDetailsPartialState.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }

    override suspend fun getCurrentUserProfileDetails(): Flow<GetProfileDetailsPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            repository.getCurrentUserProfileDetails().collect {
                when (it) {
                    is GetProfileDetailsRepositoryResponse.Success -> {
                        it.response?.let { response ->
                            emit(GetProfileDetailsPartialState.Success(response.toProfileDetailsDomain()))
                        } ?: run {
                            emit(GetProfileDetailsPartialState.Failed(genericErrorMessage))
                        }
                    }

                    is GetProfileDetailsRepositoryResponse.Failed -> {
                        emit(GetProfileDetailsPartialState.Failed(genericErrorMessage))
                    }
                }
            }
        }.safeAsync {
            when (it) {
                is IOException -> GetProfileDetailsPartialState.NoInternetConnection
                else -> GetProfileDetailsPartialState.Failed(resourceProvider.getGenericErrorMessage())
            }
        }

    override suspend fun updateUserProfileDetails(profileDetailsDomain: ProfileDetailsDomain): Flow<InteractorPartialState<String>> =
        flow {
            repository.updateCurrentUserProfileDetails(
                firstName = profileDetailsDomain.firstName,
                lastName = profileDetailsDomain.lastName,
                gender = profileDetailsDomain.gender.key,
                email = profileDetailsDomain.email
            ).collect { repositoryResult ->
                handleRepositoryResultNoTransformation(repositoryResult).collect {
                    emit(it)
                }
            }
        }

    override suspend fun updateUserProfileImage(profileImageFile: File): Flow<InteractorPartialState<String>> =
        flow {
            if (profileImageFile.extension.lowercase() !in configLogic.supportedImageFileExtensions) {
                emit(
                    InteractorPartialState.Error(
                        resourceProvider.getString(R.string.image_extension_not_supported)
                    )
                )
                return@flow
            }

            if (profileImageFile.length() > configLogic.imageFileMaxSize) {
                emit(
                    InteractorPartialState.Error(
                        resourceProvider.getString(
                            resId = R.string.image_size_too_large,
                            formatArgs = arrayOf(configLogic.imageFileMaxSize.fromBytesToMb())
                        )
                    )
                )
                return@flow
            }

            // Create multipart from file.
            val imagePart = MultipartBody.Part.createFormData(
                name = "profile_image",
                filename = "profile_image." + profileImageFile.extension,
                body = profileImageFile.toRequestBody(configLogic.supportedImageFileExtensions)
            )
            repository.updateUserProfileImage(imagePart).collect { repositoryResult ->
                handleRepositoryResultNoTransformation(repositoryResult).collect { emit(it) }
            }
        }.catch {
            when (it) {
                is IllegalArgumentException -> InteractorPartialState.Error<String>(it.message.orEmpty()) // Can be thrown from `.toRequestBody`.
                else -> InteractorPartialState.Error<String>(resourceProvider.getGenericErrorMessage())
            }
        }

    override suspend fun getPrivacySettings(): Flow<InteractorPartialState<PrivacySettingsDomain>> =
        flow {
            repository.getPrivacySettings().collect { repositoryResult ->
                handleRepositoryResult(
                    repositoryResult,
                    RepositoryPrivacySettings::toPrivacySettingsDomain
                ).collect {
                    emit(it)
                }
            }
        }

    override suspend fun updatePrivacySettings(privacySettingsDomain: PrivacySettingsDomain): Flow<InteractorPartialState<String>> =
        flow {
            repository.updatePrivacySettings(
                privacySettingsDomain.isGenderVisible,
                privacySettingsDomain.isEmailVisible
            ).collect { repositoryResult ->
                handleRepositoryResultNoTransformation(repositoryResult).collect {
                    emit(it)
                }
            }
        }
}

sealed class GetProfileDetailsPartialState {
    data class Success(val profileDetailsDomain: ProfileDetailsDomain) :
        GetProfileDetailsPartialState()

    data class Failed(val errorMessage: String) : GetProfileDetailsPartialState()

    data object NoInternetConnection : GetProfileDetailsPartialState()
}

