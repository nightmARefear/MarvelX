package com.alexgreece45.marvelx.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.RefreshTokenController
import com.alexgreece45.marvelx.data.domains.sensitiveUserData.SensitiveUserDataDomain
import com.alexgreece45.marvelx.data.domains.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.data.models.sensitiveUserData.RequestSensitiveUserData
import com.alexgreece45.marvelx.data.models.sensitiveUserData.toSensitiveUserDataDomain
import com.alexgreece45.marvelx.data.models.token.toUserAuthData
import com.alexgreece45.marvelx.data.models.user.profileDetails.toProfileDetailsDomain
import com.alexgreece45.marvelx.repository.GetProfileDetailsRepositoryResponse
import com.alexgreece45.marvelx.repository.GetSensitiveUserDataRepositoryResponse
import com.alexgreece45.marvelx.repository.GrandNewAccessTokenRepositoryResponse
import com.alexgreece45.marvelx.repository.LogoutRepositoryResponse
import com.alexgreece45.marvelx.repository.RefreshAccessTokenRepositoryResponse
import com.alexgreece45.marvelx.repository.UserRepository
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import javax.inject.Inject

interface UserInteractor {
    suspend fun logout(): Flow<LogoutPartialState>

    suspend fun grandNewAccessToken(code: String): Flow<GrandNewAccessTokenPartialState>

    suspend fun refreshAccessToken(): Flow<RefreshAccessTokenPartialState>

    suspend fun getProfileDetails(): Flow<GetProfileDetailsPartialState>

    suspend fun getSensitiveUserData(token: String): Flow<GetSensitiveUserDataPartialState>
}

class UserInteractorImpl @Inject constructor(
    private val repository: UserRepository,
    private val resourceProvider: ResourceProvider,
    private val configLogic: ConfigLogic,
    private val authController: AuthController,
    private val refreshTokenController: RefreshTokenController,
    private val prefKeys: PrefKeys,
    private val logger: AppLoggerController
) : UserInteractor {
    override suspend fun logout(): Flow<LogoutPartialState> = flow {
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()
        val clientId = configLogic.clientId
        val refreshToken = authController.getRefreshToken()

        // Disable user logged in flag.
        prefKeys.setUserLoggedIn(false)

        // Disable remember user session functionality.
        prefKeys.setRememberUserSession(false)

        // Remove stored credentials.
        authController.removeUserCredentials()

        if (refreshToken.isNullOrEmpty()) {
            emit(LogoutPartialState.Success)
            return@flow
        }

        repository.logout(clientId, refreshToken).collect { logoutRepositoryResponse ->
            // Stop refresh token worker irrespectively of the response.
            refreshTokenController.stopWorker()

            when (logoutRepositoryResponse) {
                is LogoutRepositoryResponse.Failed -> {
                    emit(
                        LogoutPartialState.Failed(
                            errorMessage = logoutRepositoryResponse.errorMessage
                                ?: genericErrorMessage
                        )
                    )
                }

                is LogoutRepositoryResponse.Success -> {
                    emit(LogoutPartialState.Success)
                }
            }
        }
    }.safeAsync {
        logger.e { "(logout) error: ${it.message.orEmpty()}" }
        when (it) {
            is IOException -> {
                LogoutPartialState.Failed(
                    errorMessage = resourceProvider.getNoInternetConnectionMessage()
                )
            }

            else -> {
                LogoutPartialState.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
    }

    override suspend fun grandNewAccessToken(code: String): Flow<GrandNewAccessTokenPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            repository.grandNewAccessToken(
                clientId = configLogic.clientId,
                clientSecret = configLogic.clientSecret,
                code = code,
                uri = configLogic.uri
            ).collect {
                when (it) {
                    is GrandNewAccessTokenRepositoryResponse.Failed -> {
                        emit(
                            GrandNewAccessTokenPartialState.Failed(
                                errorMessage = genericErrorMessage
                            )
                        )
                    }

                    is GrandNewAccessTokenRepositoryResponse.Success -> {
                        it.response?.let { response ->
                            // Save token info in device.
                            authController.setData(response.toUserAuthData())

                            // Start refresh token worker since the access token was successfully retrieved.
                            refreshTokenController.startWorker()
                            emit(
                                GrandNewAccessTokenPartialState.Success
                            )
                        } ?: emit(
                            GrandNewAccessTokenPartialState.Failed(
                                errorMessage = genericErrorMessage
                            )
                        )
                    }
                }
            }
        }.safeAsync {
            logger.e { "(grandNewAccessToken) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> GrandNewAccessTokenPartialState.Failed(
                    errorMessage = resourceProvider.getNoInternetConnectionMessage()
                )

                else -> {
                    GrandNewAccessTokenPartialState.Failed(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun refreshAccessToken(): Flow<RefreshAccessTokenPartialState> = flow {
        val clientId = configLogic.clientId
        val token = authController.getRefreshToken()
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()

        if (token.isNullOrBlank()) {
            // Remove stored credentials.
            authController.removeUserCredentials()
            emit(RefreshAccessTokenPartialState.Failed(errorMessage = genericErrorMessage))
            return@flow
        }

        repository.refreshAccessToken(clientId, token).collect {
            when (it) {
                is RefreshAccessTokenRepositoryResponse.Failed -> {
                    // Remove stored credentials.
                    authController.removeUserCredentials()
                    emit(
                        RefreshAccessTokenPartialState.Failed(
                            errorMessage = genericErrorMessage
                        )
                    )
                }

                is RefreshAccessTokenRepositoryResponse.Success -> {
                    it.response?.let { tokenResponse ->
                        // Save token info to device.
                        authController.setData(tokenResponse.toUserAuthData())
                        emit(RefreshAccessTokenPartialState.Success)
                    } ?: run {
                        // Remove stored credentials.
                        authController.removeUserCredentials()
                        emit(
                            RefreshAccessTokenPartialState.Failed(
                                errorMessage = genericErrorMessage
                            )
                        )
                    }
                }
            }
        }
    }.safeAsync {
        logger.e { "(refreshAccessToken) error: ${it.message.orEmpty()}" }
        when (it) {
            is IOException -> RefreshAccessTokenPartialState.Failed(
                errorMessage = resourceProvider.getNoInternetConnectionMessage()
            )

            else -> {
                RefreshAccessTokenPartialState.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
    }

    override suspend fun getProfileDetails(): Flow<GetProfileDetailsPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            repository.getProfileDetails(prefKeys.getUserId()).collect {
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
                is IOException -> GetProfileDetailsPartialState.NoInternetConnection(
                    errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                )

                else -> GetProfileDetailsPartialState.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }

    override suspend fun getSensitiveUserData(token: String): Flow<GetSensitiveUserDataPartialState> =
        flow {
            val request = RequestSensitiveUserData(token = token)

            repository.getSensitiveUserData(request).collect { repositoryResponse ->
                when (repositoryResponse) {
                    is GetSensitiveUserDataRepositoryResponse.Failed -> {
                        emit(
                            GetSensitiveUserDataPartialState.Failed(
                                errorMessage = repositoryResponse.responseUserError.errorMessage
                            )
                        )
                    }

                    is GetSensitiveUserDataRepositoryResponse.Success -> {
                        repositoryResponse.response?.let { responseSensitiveUserData ->
                            // If the OTP functionality is blocked, but there's no expiresIn info,
                            // throw an "internal server error".
                            if (responseSensitiveUserData.isOtpBlocked &&
                                responseSensitiveUserData.otpBlockExpiresIn == null
                            ) {
                                emit(
                                    GetSensitiveUserDataPartialState.Failed(
                                        errorMessage = resourceProvider.getString(R.string.internal_server_error)
                                    )
                                )
                                return@collect
                            }

                            emit(
                                GetSensitiveUserDataPartialState.Success(
                                    sensitiveUserDataDomain = responseSensitiveUserData.toSensitiveUserDataDomain()
                                )
                            )
                        } ?: emit(
                            GetSensitiveUserDataPartialState.Failed(
                                errorMessage = resourceProvider.getGenericErrorMessage()
                            )
                        )
                    }
                }
            }
        }.safeAsync {
            logger.e { "(getSensitiveUserData) error: ${it.message}" }
            when (it) {
                is IOException -> GetSensitiveUserDataPartialState.Failed(errorMessage = resourceProvider.getNoInternetConnectionMessage())
                else -> GetSensitiveUserDataPartialState.Failed(errorMessage = resourceProvider.getGenericErrorMessage())
            }
        }
}

sealed class GetProfileDetailsPartialState {
    data class Success(val profileDetailsDomain: ProfileDetailsDomain) :
        GetProfileDetailsPartialState()

    data class Failed(val errorMessage: String) : GetProfileDetailsPartialState()

    data class NoInternetConnection(val errorMessage: String) : GetProfileDetailsPartialState()
}

sealed class GrandNewAccessTokenPartialState {
    data object Success : GrandNewAccessTokenPartialState()
    data class Failed(val errorMessage: String) : GrandNewAccessTokenPartialState()
}

sealed class RefreshAccessTokenPartialState {
    data object Success : RefreshAccessTokenPartialState()
    data class Failed(val errorMessage: String) : RefreshAccessTokenPartialState()
}

sealed class LogoutPartialState {
    data object Success : LogoutPartialState()
    data class Failed(val errorMessage: String) : LogoutPartialState()
}

sealed class GetSensitiveUserDataPartialState {
    data class Success(val sensitiveUserDataDomain: SensitiveUserDataDomain) :
        GetSensitiveUserDataPartialState()

    data class Failed(val errorMessage: String) : GetSensitiveUserDataPartialState()
}