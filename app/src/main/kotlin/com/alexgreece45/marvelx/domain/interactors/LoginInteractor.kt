package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.configuration.ConfigOauth
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.CryptoController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.RefreshTokenController
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.data.repository.AuthorizeRepositoryPartialState
import com.alexgreece45.marvelx.data.repository.GetSensitiveUserDataRepositoryResponse
import com.alexgreece45.marvelx.data.repository.GrandNewAccessTokenRepositoryResponse
import com.alexgreece45.marvelx.data.repository.LoginRepository
import com.alexgreece45.marvelx.data.repository.LogoutRepositoryResponse
import com.alexgreece45.marvelx.data.repository.PostLoginRepositoryResponse
import com.alexgreece45.marvelx.data.repository.RefreshAccessTokenRepositoryResponse
import com.alexgreece45.marvelx.domain.model_api.sensitiveUserData.SensitiveUserDataDomain
import com.alexgreece45.marvelx.domain.model_api.user.postLogin.SignData
import com.alexgreece45.marvelx.domain.model_api.user.profileDetails.ProfileDetailsDomain
import com.alexgreece45.marvelx.domain.model_api.userAuthData.UserAuthData
import com.alexgreece45.marvelx.network_logic.models_api.sensitiveUserData.RequestSensitiveUserData
import com.alexgreece45.marvelx.network_logic.models_api.sensitiveUserData.toSensitiveUserDataDomain
import com.alexgreece45.marvelx.network_logic.models_api.token.toUserAuthData
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

interface LoginInteractor {
    /**
     * Authorization call for the OAuth 2.0 Grant flow. It sends the code challenge and challenge
     * method and receives a `code` that is used to correlate the code challenge to this device. The
     * [tempAuthCode] is the temporary code received by the server to protect attacks against
     * generating tokens for any user.
     *
     * The received `code` is later used to obtain the access token.
     */
    suspend fun authorize(tempAuthCode: String): Flow<AuthorizePartialState>

    /**
     * The logout call. It performs all the necessary operations to wipe the local data, and to
     * close the websocket connection, and much more.
     */
    suspend fun logout(): Flow<LogoutPartialState>

    /**
     * The post-login call. It retrieves all the necessary data for signing requests.
     */
    suspend fun postLogin(accessToken: String): Flow<PostLoginPartialState>

    /**
     * Grants an access token for the OAuth 2.0 flow. It sends the code verifier and the `code` that
     * was received earlier. If the response is successful, then that means that the access token
     * was successfully retrieved.
     */
    suspend fun grandNewAccessToken(authCode: String): Flow<GrandNewAccessTokenPartialState>

    suspend fun refreshAccessToken(): Flow<RefreshAccessTokenPartialState>

    suspend fun getSensitiveUserData(token: String): Flow<GetSensitiveUserDataPartialState>
}

class LoginInteractorImpl(
    private val repository: LoginRepository,
    private val resourceProvider: ResourceProvider,
    private val configOauth: ConfigOauth,
    private val authController: AuthController,
    private val refreshTokenController: RefreshTokenController,
    private val prefKeys: PrefKeys,
    private val logger: AppLogger,
    private val websocketController: WebsocketController,
    private val cryptoController: CryptoController
) : LoginInteractor {

    private val codeVerifier by lazy {
        cryptoController.createCodeVerifier()
    }

    private val codeChallenge by lazy {
        cryptoController.createCodeChallenge(codeVerifier)
    }

    override suspend fun authorize(tempAuthCode: String): Flow<AuthorizePartialState> = flow {
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()
        repository.authorize(tempAuthCode, configOauth.clientId, codeChallenge, configOauth.codeChallengeMethod)
            .collect {
                when (it) {
                    is AuthorizeRepositoryPartialState.Success -> {
                        when (val body = it.response) {
                            null -> emit(AuthorizePartialState.Failed(genericErrorMessage))
                            else -> {
                                if (body.authCode.isBlank()) {
                                    emit(AuthorizePartialState.Failed(genericErrorMessage))
                                } else {
                                    emit(AuthorizePartialState.Success(body.authCode))
                                }
                            }
                        }
                    }

                    is AuthorizeRepositoryPartialState.Failed -> {
                        emit(
                            AuthorizePartialState.Failed(
                                genericErrorMessage
                            )
                        )
                    }
                }
            }
    }.safeAsync {
        when (it) {
            is IOException -> AuthorizePartialState.Failed(resourceProvider.getNoInternetConnectionMessage())
            else -> AuthorizePartialState.Failed(resourceProvider.getGenericErrorMessage())
        }
    }

    override suspend fun logout(): Flow<LogoutPartialState> = flow {
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()
        val clientId = configOauth.clientId
        val refreshToken = authController.getRefreshToken()

        // Close websocket connection.
        websocketController.close()

        // Disable user logged in flag.
        prefKeys.setUserLoggedIn(false)

        // Disable remember user session functionality.
        prefKeys.setRememberUserSession(false)

        // Remove signing data.
        prefKeys.signData = null

        // Remove auto-logout timestamp.
        prefKeys.autoLogoutTimestamp = null

        // Remove stored credentials.
        authController.removeUserCredentials()

        if (refreshToken.isNullOrEmpty()) {
            emit(LogoutPartialState.Success)
            return@flow
        }

        repository.logout(configOauth.clientId, refreshToken).collect { logoutRepositoryResponse ->
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
        logger.e("LoginInteractor") { "(logout) error: ${it.message.orEmpty()}" }
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

    override suspend fun postLogin(accessToken: String): Flow<PostLoginPartialState> = flow {
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()

        repository.postLogin(accessToken).collect {
            when (it) {
                is PostLoginRepositoryResponse.Success -> {
                    val sign = it.response?.sign
                    if (sign == null) {
                        emit(PostLoginPartialState.Failed(genericErrorMessage))
                        return@collect
                    }
                    if (sign.key.isNullOrBlank() || sign.algorithm.isNullOrBlank()) {
                        emit(PostLoginPartialState.Failed(genericErrorMessage))
                        return@collect
                    }

                    prefKeys.signData = SignData(key = sign.key, algorithm = sign.algorithm)
                    emit(PostLoginPartialState.Success)
                }

                is PostLoginRepositoryResponse.Failed -> {
                    emit(PostLoginPartialState.Failed(errorMessage = genericErrorMessage))
                }
            }
        }
    }.safeAsync {
        logger.e("LoginInteractor") { "(postLogin) error: ${it.message.orEmpty()}" }
        when (it) {
            is IOException -> {
                PostLoginPartialState.Failed(
                    errorMessage = resourceProvider.getNoInternetConnectionMessage()
                )
            }

            else -> {
                PostLoginPartialState.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
    }

    override suspend fun grandNewAccessToken(authCode: String): Flow<GrandNewAccessTokenPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            repository.grandNewAccessToken(
                clientId = configOauth.clientId,
                code = authCode,
                codeVerifier = codeVerifier,
                uri = configOauth.redirectUri
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
                            emit(GrandNewAccessTokenPartialState.Success(response.toUserAuthData()))
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
        val clientId = configOauth.clientId
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

sealed interface AuthorizePartialState {
    data class Success(val authCode: String) : AuthorizePartialState
    data class Failed(val errorMessage: String) : AuthorizePartialState
}

sealed interface PostLoginPartialState {
    data object Success : PostLoginPartialState

    data class Failed(val errorMessage: String) : PostLoginPartialState
}

sealed class GrandNewAccessTokenPartialState {
    data class Success(val userAuthData: UserAuthData) : GrandNewAccessTokenPartialState()
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