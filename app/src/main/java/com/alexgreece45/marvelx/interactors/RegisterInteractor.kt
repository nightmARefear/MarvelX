package com.alexgreece45.marvelx.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.RefreshTokenController
import com.alexgreece45.marvelx.data.domains.jwtSensitiveUserData.JwtSensitiveUserDataDomain
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.domains.user.login.LoginResponseDomain
import com.alexgreece45.marvelx.data.domains.user.loginWithToken.LoginWithTokenResponseDomain
import com.alexgreece45.marvelx.data.domains.user.signup.SignupResponseDomain
import com.alexgreece45.marvelx.data.models.jwtSensitiveUserData.RequestJwtSensitiveUserData
import com.alexgreece45.marvelx.data.models.jwtSensitiveUserData.toJwtSensitiveUserDataDomain
import com.alexgreece45.marvelx.data.models.user.login.UserLoginRequest
import com.alexgreece45.marvelx.data.models.user.login.toUserLoginResponseDomain
import com.alexgreece45.marvelx.data.models.user.loginWithToken.toUserLoginWithTokenResponseDomain
import com.alexgreece45.marvelx.data.models.user.signup.UserSignupRequest
import com.alexgreece45.marvelx.data.models.user.signup.toSignupResponseDomain
import com.alexgreece45.marvelx.repository.GetJwtForSensitiveUserDataRepositoryResponse
import com.alexgreece45.marvelx.repository.PostLoginRepositoryResponse
import com.alexgreece45.marvelx.repository.PostLoginWithTokenRepositoryResponse
import com.alexgreece45.marvelx.repository.PostSignupRepositoryResponse
import com.alexgreece45.marvelx.repository.RegisterRepository
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

interface RegisterInteractor {
    suspend fun postSignup(userSignupRequest: UserSignupRequest): Flow<PostSignupPartialState>

    suspend fun postLogin(email: String, password: String): Flow<PostLoginPartialState>

    suspend fun postLoginWithToken(): Flow<PostLoginWithTokenPartialState>

    suspend fun getJwtForSensitiveUserData(
        userId: Int,
        verificationStep: VerificationStep
    ): Flow<GetJwtForSensitiveUserDataPartialState>
}

class RegisterInteractorImpl(
    private val repository: RegisterRepository,
    private val resourceProvider: ResourceProvider,
    private val refreshTokenController: RefreshTokenController,
    private val logger: AppLoggerController
) : RegisterInteractor {
    override suspend fun postSignup(userSignupRequest: UserSignupRequest): Flow<PostSignupPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            repository.postSignup(userSignupRequest).collect {
                when (it) {
                    is PostSignupRepositoryResponse.Failed -> {
                        emit(
                            PostSignupPartialState.Failed(
                                errorMessage = it.responseUserError.errorMessage
                            )
                        )
                    }

                    is PostSignupRepositoryResponse.Success -> {
                        it.response?.let { signupResponse ->
                            emit(
                                PostSignupPartialState.Success(
                                    signupResponseDomain = signupResponse.toSignupResponseDomain()
                                )
                            )
                        } ?: emit(
                            PostSignupPartialState.Failed(
                                errorMessage = genericErrorMessage
                            )
                        )
                    }
                }
            }
        }.safeAsync {
            logger.e { "(postSignup) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> {
                    PostSignupPartialState.Failed(
                        errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                    )
                }

                else -> {
                    PostSignupPartialState.Failed(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun postLogin(email: String, password: String): Flow<PostLoginPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            val userLoginRequest = UserLoginRequest(
                email = email,
                password = password,
                epoch = DateUtil.getSecondsFromEpoch()
            )
            repository.postLogin(userLoginRequest).collect {
                when (it) {
                    is PostLoginRepositoryResponse.Failed -> {
                        emit(PostLoginPartialState.Failed(errorMessage = it.responseUserError.errorMessage))
                    }

                    is PostLoginRepositoryResponse.Success -> {
                        it.response?.let { loginResponse ->
                            val userLoginResponseDomain = loginResponse.toUserLoginResponseDomain()
                            if (userLoginResponseDomain.verificationStep is VerificationStep.None) {
                                // Verification step cannot be null for `postLogin`. Propagate error to client.
                                emit(PostLoginPartialState.Failed(errorMessage = genericErrorMessage))
                                return@collect
                            }

                            emit(
                                PostLoginPartialState.Success(
                                    loginResponseDomain = userLoginResponseDomain
                                )
                            )
                        } ?: emit(PostLoginPartialState.Failed(errorMessage = genericErrorMessage))
                    }
                }
            }
        }.safeAsync {
            logger.e { "(postLogin) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> {
                    PostLoginPartialState.Failed(
                        errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                    )
                }

                else -> {
                    PostLoginPartialState.Failed(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun postLoginWithToken(): Flow<PostLoginWithTokenPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()

            repository.postLoginWithToken().collect {
                when (it) {
                    is PostLoginWithTokenRepositoryResponse.Failed -> {
                        // Propagate generic error to client.
                        emit(PostLoginWithTokenPartialState.Failed(errorMessage = genericErrorMessage))
                    }

                    is PostLoginWithTokenRepositoryResponse.Success -> {
                        it.response?.let { loginWithTokenResponse ->
                            val userLoginWithTokenResponseDomain =
                                loginWithTokenResponse.toUserLoginWithTokenResponseDomain()

                            // Start refresh token worker if there is no verification step pending (i.e., `VerificationStep.None`).
                            if (userLoginWithTokenResponseDomain.verificationStep is VerificationStep.None) {
                                refreshTokenController.startWorker()
                            }

                            emit(
                                PostLoginWithTokenPartialState.Success(
                                    loginWithTokenResponseDomain = userLoginWithTokenResponseDomain
                                )
                            )
                        }
                            ?: emit(PostLoginWithTokenPartialState.Failed(errorMessage = genericErrorMessage))
                    }
                }
            }
        }.safeAsync {
            logger.e("RegisterInteractor") { "(postLoginWithToken) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> {
                    PostLoginWithTokenPartialState.Failed(
                        errorMessage = resourceProvider.getNoInternetConnectionMessage()
                    )
                }

                else -> {
                    PostLoginWithTokenPartialState.Failed(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun getJwtForSensitiveUserData(
        userId: Int,
        verificationStep: VerificationStep
    ): Flow<GetJwtForSensitiveUserDataPartialState> = flow {
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()
        val request =
            RequestJwtSensitiveUserData(
                userId = userId,
                verificationStep = verificationStep.key
            )

        repository.getJwtForSensitiveUserData(request).collect {
            when (it) {
                is GetJwtForSensitiveUserDataRepositoryResponse.Failed -> {
                    emit(
                        GetJwtForSensitiveUserDataPartialState.Failed(
                            errorMessage = it.responseUserError.errorMessage
                        )
                    )
                }

                is GetJwtForSensitiveUserDataRepositoryResponse.Success -> {
                    it.response?.let { responseJwtSensitiveUserData ->
                        emit(
                            GetJwtForSensitiveUserDataPartialState.Success(
                                jwtSensitiveUserDataDomain = responseJwtSensitiveUserData.toJwtSensitiveUserDataDomain()
                            )
                        )
                    } ?: emit(
                        GetJwtForSensitiveUserDataPartialState.Failed(
                            errorMessage = genericErrorMessage
                        )
                    )
                }
            }
        }
    }.safeAsync {
        logger.e { "(getJwtForSensitiveUserData) error: ${it.message.orEmpty()}" }
        when (it) {
            is IOException -> {
                GetJwtForSensitiveUserDataPartialState.Failed(
                    errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                )
            }

            else -> {
                GetJwtForSensitiveUserDataPartialState.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
    }
}

sealed class PostSignupPartialState {
    data class Success(val signupResponseDomain: SignupResponseDomain) : PostSignupPartialState()

    data class Failed(val errorMessage: String) : PostSignupPartialState()
}

sealed class PostLoginPartialState {
    data class Success(val loginResponseDomain: LoginResponseDomain) :
        PostLoginPartialState()

    data class Failed(val errorMessage: String) : PostLoginPartialState()
}

sealed class PostLoginWithTokenPartialState {
    data class Success(val loginWithTokenResponseDomain: LoginWithTokenResponseDomain) :
        PostLoginWithTokenPartialState()

    data class Failed(val errorMessage: String) : PostLoginWithTokenPartialState()
}

sealed class GetJwtForSensitiveUserDataPartialState {
    data class Success(val jwtSensitiveUserDataDomain: JwtSensitiveUserDataDomain) :
        GetJwtForSensitiveUserDataPartialState()

    data class Failed(val errorMessage: String) : GetJwtForSensitiveUserDataPartialState()
}