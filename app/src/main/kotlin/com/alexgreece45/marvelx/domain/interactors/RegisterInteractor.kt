package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.network_logic.models_api.jwtSensitiveUserData.RequestJwtSensitiveUserData
import com.alexgreece45.marvelx.network_logic.models_api.jwtSensitiveUserData.toJwtSensitiveUserDataDomain
import com.alexgreece45.marvelx.network_logic.models_api.user.login.RequestUserLogin
import com.alexgreece45.marvelx.network_logic.models_api.user.login.toUserLoginResponseDomain
import com.alexgreece45.marvelx.network_logic.models_api.user.loginWithToken.toUserLoginWithTokenResponseDomain
import com.alexgreece45.marvelx.network_logic.models_api.user.signup.RequestUserSignup
import com.alexgreece45.marvelx.domain.model_api.jwtSensitiveUserData.JwtSensitiveUserDataDomain
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep
import com.alexgreece45.marvelx.domain.model_api.user.login.LoginResponseDomain
import com.alexgreece45.marvelx.domain.model_api.user.loginWithToken.LoginWithTokenResponseDomain
import com.alexgreece45.marvelx.domain.model_api.user.signup.SignupDomain
import com.alexgreece45.marvelx.domain.model_api.user.signup.toSignupDomain
import com.alexgreece45.marvelx.data.repository.GetJwtForSensitiveUserDataRepositoryResponse
import com.alexgreece45.marvelx.data.repository.LoginRepositoryResponse
import com.alexgreece45.marvelx.data.repository.PostLoginWithTokenRepositoryResponse
import com.alexgreece45.marvelx.data.repository.RegisterRepository
import com.alexgreece45.marvelx.data.repository.SignupRepositoryPartialState
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException

interface RegisterInteractor {
    suspend fun signup(requestUserSignup: RequestUserSignup): Flow<SignupPartialState>

    /**
     * The login call. Attempts to login the user by sending the given [email] and [password].
     */
    suspend fun login(email: String, password: String): Flow<LoginPartialState>

    suspend fun loginWithToken(): Flow<LoginWithTokenPartialState>

    suspend fun getJwtForSensitiveUserData(
        userId: Int,
        verificationStep: VerificationStep
    ): Flow<GetJwtForSensitiveUserDataPartialState>
}

class RegisterInteractorImpl(
    private val logger: AppLogger,
    private val repository: RegisterRepository,
    private val resourceProvider: ResourceProvider
) : RegisterInteractor {
    override suspend fun signup(requestUserSignup: RequestUserSignup): Flow<SignupPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            repository.postSignup(requestUserSignup).collect {
                when (it) {
                    is SignupRepositoryPartialState.Failed -> {
                        emit(
                            SignupPartialState.Failed(
                                errorMessage = it.responseUserError.errorMessage
                            )
                        )
                    }

                    is SignupRepositoryPartialState.Success -> {
                        val signupResponse = it.response
                        if (signupResponse == null) {
                            emit(
                                SignupPartialState.Failed(
                                    errorMessage = genericErrorMessage
                                )
                            )
                            return@collect
                        }

                        val signupDomain = signupResponse.toSignupDomain()
                        if (signupDomain.verificationStep is VerificationStep.None
                            && signupDomain.authCode.isBlank()
                        ) {
                            emit(
                                SignupPartialState.Failed(
                                    errorMessage = genericErrorMessage
                                )
                            )
                            return@collect
                        }

                        emit(
                            SignupPartialState.Success(
                                signupDomain = signupDomain
                            )
                        )
                    }
                }
            }
        }.safeAsync {
            logger.e { "(postSignup) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> {
                    SignupPartialState.Failed(
                        errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                    )
                }

                else -> {
                    SignupPartialState.Failed(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun login(email: String, password: String): Flow<LoginPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()
            val requestUserLogin = RequestUserLogin(
                email = email,
                password = password
            )
            repository.login(requestUserLogin).collect {
                when (it) {
                    is LoginRepositoryResponse.Failed -> {
                        emit(LoginPartialState.Failed(errorMessage = it.responseUserError.errorMessage))
                    }

                    is LoginRepositoryResponse.Success -> {
                        it.response?.let { loginResponse ->
                            val userLoginResponseDomain = loginResponse.toUserLoginResponseDomain()
                            if (userLoginResponseDomain.verificationStep is VerificationStep.None) {
                                // Verification step cannot be null for `login`. Propagate error to client.
                                emit(LoginPartialState.Failed(errorMessage = genericErrorMessage))
                                return@collect
                            }

                            emit(
                                LoginPartialState.Success(
                                    loginResponseDomain = userLoginResponseDomain
                                )
                            )
                        } ?: emit(LoginPartialState.Failed(errorMessage = genericErrorMessage))
                    }
                }
            }
        }.safeAsync {
            logger.e { "(login) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> {
                    LoginPartialState.Failed(
                        errorMessage = resourceProvider.getNoInternetConnectionMessage()
                    )
                }

                else -> {
                    LoginPartialState.Failed(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun loginWithToken(): Flow<LoginWithTokenPartialState> =
        flow {
            val genericErrorMessage = resourceProvider.getGenericErrorMessage()

            repository.loginWithToken().collect {
                when (it) {
                    is PostLoginWithTokenRepositoryResponse.Failed -> {
                        // Propagate generic error to client.
                        emit(LoginWithTokenPartialState.Failed(errorMessage = genericErrorMessage))
                    }

                    is PostLoginWithTokenRepositoryResponse.Success -> {
                        it.response?.let { loginWithTokenResponse ->
                            val userLoginWithTokenResponseDomain =
                                loginWithTokenResponse.toUserLoginWithTokenResponseDomain()

                            emit(
                                LoginWithTokenPartialState.Success(
                                    loginWithTokenResponseDomain = userLoginWithTokenResponseDomain
                                )
                            )
                        }
                            ?: emit(LoginWithTokenPartialState.Failed(errorMessage = genericErrorMessage))
                    }
                }
            }
        }.safeAsync {
            logger.e("RegisterInteractor") { "(postLoginWithToken) error: ${it.message.orEmpty()}" }
            when (it) {
                is IOException -> {
                    LoginWithTokenPartialState.Failed(
                        errorMessage = resourceProvider.getNoInternetConnectionMessage()
                    )
                }

                else -> {
                    LoginWithTokenPartialState.Failed(
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

sealed class SignupPartialState {
    data class Success(val signupDomain: SignupDomain) : SignupPartialState()

    data class Failed(val errorMessage: String) : SignupPartialState()
}

sealed class LoginPartialState {
    data class Success(val loginResponseDomain: LoginResponseDomain) :
        LoginPartialState()

    data class Failed(val errorMessage: String) : LoginPartialState()
}

sealed class LoginWithTokenPartialState {
    data class Success(val loginWithTokenResponseDomain: LoginWithTokenResponseDomain) :
        LoginWithTokenPartialState()

    data class Failed(val errorMessage: String) : LoginWithTokenPartialState()
}

sealed class GetJwtForSensitiveUserDataPartialState {
    data class Success(val jwtSensitiveUserDataDomain: JwtSensitiveUserDataDomain) :
        GetJwtForSensitiveUserDataPartialState()

    data class Failed(val errorMessage: String) : GetJwtForSensitiveUserDataPartialState()
}