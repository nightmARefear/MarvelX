package com.alexgreece45.marvelx.domain.interactors

import android.util.Base64
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.extensions.LOGGER_TAG
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.core.network.model.api.sensitiveUserData.RequestSensitiveUserData
import com.alexgreece45.marvelx.core.network.model.api.token.toModel
import com.alexgreece45.marvelx.core.network.storage.SignatureStorage
import com.alexgreece45.marvelx.data.repository.AuthorizeRepositoryPartialState
import com.alexgreece45.marvelx.data.repository.GetSensitiveUserDataRepositoryResponse
import com.alexgreece45.marvelx.data.repository.GrandNewAccessTokenRepositoryResponse
import com.alexgreece45.marvelx.data.repository.LoginRepository
import com.alexgreece45.marvelx.data.repository.PostLoginRepositoryResponse
import com.alexgreece45.marvelx.data.repository.RefreshTokenRepository
import com.alexgreece45.marvelx.data.repository.TokenRefreshRepositoryResult
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.domain.model_api.sensitiveUserData.SensitiveUserDataDomain
import com.alexgreece45.marvelx.domain.model_api.sensitiveUserData.toSensitiveUserDataDomain
import com.alexgreece45.marvelx.domain.usecases.RemoveUserCredentialsUseCase
import com.alexgreece45.marvelx.model.model.AuthenticationTokens
import com.alexgreece45.marvelx.model.model.Signature
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import org.koin.core.annotation.Factory
import java.security.MessageDigest
import java.security.SecureRandom

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

@Factory
class LoginInteractorImpl(
    private val loginRepository: LoginRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val resourceProvider: ResourceProvider,
    private val configOauthLogic: ConfigOauthLogic,
    private val userSessionRepository: UserSessionRepository,
    private val signatureStorage: SignatureStorage,
    private val removeUserCredentialsUseCase: RemoveUserCredentialsUseCase,
    private val appLogger: AppLogger,
    private val userRepository: UserRepository,
) : LoginInteractor {

    private val codeVerifier by lazy {
        createCodeVerifier()
    }

    private val codeChallenge by lazy {
        createCodeChallenge(codeVerifier)
    }

    override suspend fun authorize(tempAuthCode: String): Flow<AuthorizePartialState> = flow {
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()
        loginRepository.authorize(
            tempAuthCode,
            configOauthLogic.clientId,
            codeChallenge,
            configOauthLogic.codeChallengeMethod
        ).collect {
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

    override suspend fun postLogin(accessToken: String): Flow<PostLoginPartialState> = flow {
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()

        loginRepository.postLogin(accessToken).collect {
            when (it) {
                is PostLoginRepositoryResponse.Success -> {
                    val sign = it.response?.sign
                    if (sign == null
                        || sign.key.isNullOrBlank()
                        || sign.algorithm.isNullOrBlank()
                    ) {
                        emit(PostLoginPartialState.Failed(genericErrorMessage))
                        return@collect
                    }

                    signatureStorage.setSignature(
                        Signature(
                            key = sign.key.orEmpty(),
                            algorithm = sign.algorithm.orEmpty()
                        )
                    )
                    userRepository.setUserLoggedIn(true)
                    emit(PostLoginPartialState.Success)
                }

                is PostLoginRepositoryResponse.Failed -> {
                    emit(PostLoginPartialState.Failed(errorMessage = genericErrorMessage))
                }
            }
        }
    }.safeAsync {
        appLogger.e("LoginInteractor") { "(postLogin) error: ${it.message.orEmpty()}" }
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
            loginRepository.grandNewAccessToken(
                clientId = configOauthLogic.clientId,
                code = authCode,
                codeVerifier = createCodeVerifier(),
                uri = configOauthLogic.redirectUri
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
                        when (val response = it.response) {
                            null -> {
                                GrandNewAccessTokenPartialState.Failed(
                                    errorMessage = genericErrorMessage
                                )
                            }

                            else -> {
                                emit(GrandNewAccessTokenPartialState.Success(response.toModel()))
                            }
                        }
                    }
                }
            }
        }.safeAsync {
            appLogger.e(LOGGER_TAG) { "(grandNewAccessToken) error: ${it.message.orEmpty()}" }
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
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()
        val clientId = configOauthLogic.clientId
        val refreshToken = userSessionRepository.getRefreshToken()

        if (refreshToken.isNullOrBlank()) {
            // Remove stored credentials.
            removeUserCredentialsUseCase()
            emit(RefreshAccessTokenPartialState.Failed(errorMessage = genericErrorMessage))
            return@flow
        }

        refreshTokenRepository.refreshAccessToken(clientId, refreshToken).let {
            when (it) {
                is TokenRefreshRepositoryResult.Failed -> {
                    // Remove stored credentials.
                    removeUserCredentialsUseCase()
                    emit(
                        RefreshAccessTokenPartialState.Failed(
                            errorMessage = genericErrorMessage
                        )
                    )
                }

                is TokenRefreshRepositoryResult.Success -> {
                    when (val response = it.responseToken) {
                        null -> {
                            // Remove stored credentials.
                            removeUserCredentialsUseCase()
                            emit(
                                RefreshAccessTokenPartialState.Failed(
                                    errorMessage = genericErrorMessage
                                )
                            )
                        }

                        else -> {
                            // Save token info to device.
                            userSessionRepository.setAuthenticationTokens(response.toModel())
                            emit(RefreshAccessTokenPartialState.Success)
                        }
                    }
                }
            }
        }
    }.safeAsync {
        appLogger.e { "(refreshAccessToken) error: ${it.message.orEmpty()}" }
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

            loginRepository.getSensitiveUserData(request).collect { repositoryResponse ->
                when (repositoryResponse) {
                    is GetSensitiveUserDataRepositoryResponse.Failed -> {
                        emit(
                            GetSensitiveUserDataPartialState.Failed(
                                errorMessage = repositoryResponse.responseUserError.errorMessage
                            )
                        )
                    }

                    is GetSensitiveUserDataRepositoryResponse.Success -> {
                        when (val responseSensitiveUserData = repositoryResponse.response) {
                            null -> {
                                emit(
                                    GetSensitiveUserDataPartialState.Failed(
                                        errorMessage = resourceProvider.getGenericErrorMessage()
                                    )
                                )
                            }

                            else -> {
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
                            }
                        }
                    }
                }
            }
        }.safeAsync {
            appLogger.e { "(getSensitiveUserData) error: ${it.message}" }
            when (it) {
                is IOException -> GetSensitiveUserDataPartialState.Failed(errorMessage = resourceProvider.getNoInternetConnectionMessage())
                else -> GetSensitiveUserDataPartialState.Failed(errorMessage = resourceProvider.getGenericErrorMessage())
            }
        }

    private fun createCodeVerifier(): String {
        val code = ByteArray(64)
        SecureRandom().apply {
            nextBytes(code)
        }
        return Base64.encodeToString(code, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private fun createCodeChallenge(verifier: String): String {
        val bytes = verifier.toByteArray(Charsets.UTF_8)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes, 0, bytes.size)
        return Base64.encodeToString(
            messageDigest.digest(),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
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
    data class Success(val authenticationTokens: AuthenticationTokens) :
        GrandNewAccessTokenPartialState()

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
