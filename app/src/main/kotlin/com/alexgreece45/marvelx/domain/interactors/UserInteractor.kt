package com.alexgreece45.marvelx.domain.interactors

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
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

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
     * Gets the privacy settings of the current user.
     */
    suspend fun getPrivacySettings(): Flow<InteractorPartialState<PrivacySettingsDomain>>

    /**
     * Updates the privacy settings of the current user.
     */
    suspend fun updatePrivacySettings(privacySettingsDomain: PrivacySettingsDomain): Flow<InteractorPartialState<String>>
}

class UserInteractorImpl(
    private val repository: UserRepository,
    private val resourceProvider: ResourceProvider,
    private val logger: AppLogger
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
                            ?: emit(GetProfileDetailsPartialState.Failed(errorMessage = genericErrorMessage))
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

sealed interface UpdateProfileDetailsPartialState {
    data object Success : UpdateProfileDetailsPartialState
    data object Failed : UpdateProfileDetailsPartialState
    data object NoInternetConnection : UpdateProfileDetailsPartialState
}

sealed interface UpdatePrivacySettingsPartialState {
    data object Success : UpdatePrivacySettingsPartialState
    data object Failed : UpdatePrivacySettingsPartialState
    data object NoInternetConnection : UpdatePrivacySettingsPartialState
}
