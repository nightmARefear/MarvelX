package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.domains.user.login.UserLoginResponseDomain
import com.alexgreece45.marvelx.data.domains.user.loginWithToken.UserLoginWithTokenResponseDomain
import com.alexgreece45.marvelx.data.domains.user.profileDetails.UserProfileDetailsDomain
import com.alexgreece45.marvelx.data.models.token.TokenResponse
import com.alexgreece45.marvelx.data.models.token.toUserAuthData
import com.alexgreece45.marvelx.data.models.user.UserResponseError
import com.alexgreece45.marvelx.data.models.user.login.UserLoginRequest
import com.alexgreece45.marvelx.data.models.user.login.toUserLoginResponseDomain
import com.alexgreece45.marvelx.data.models.user.loginWithToken.UserLoginWithTokenRequest
import com.alexgreece45.marvelx.data.models.user.loginWithToken.toUserLoginWithTokenResponseDomain
import com.alexgreece45.marvelx.data.models.user.profileDetails.toUserProfileDetailsDomain
import com.alexgreece45.marvelx.data.models.user.signUp.SignupResponse
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignupRequest
import com.alexgreece45.marvelx.domain.controllers.AuthController
import com.alexgreece45.marvelx.domain.controllers.ConfigController
import com.alexgreece45.marvelx.domain.repositories.GrandNewAccessTokenRepositoryResponse
import com.alexgreece45.marvelx.domain.repositories.LogoutRepositoryResponse
import com.alexgreece45.marvelx.domain.repositories.RefreshAccessTokenRepositoryResponse
import com.alexgreece45.marvelx.domain.repositories.UserRepository
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import logcat.logcat
import okhttp3.ResponseBody
import okio.IOException
import javax.inject.Inject

interface UserInteractor {
    suspend fun postSignUp(userSignupRequest: UserSignupRequest): Flow<PostSignUpPartialState>

    suspend fun postLogin(userLoginRequest: UserLoginRequest): Flow<PostLoginPartialState>

    suspend fun postLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Flow<PostLoginWithTokenPartialState>

    suspend fun logout(): Flow<LogoutPartialState>

    suspend fun grandNewAccessToken(code: String): Flow<GrandNewAccessTokenPartialState>

    suspend fun refreshAccessToken(): Flow<RefreshAccessTokenPartialState>

    suspend fun getProfileDetails(userId: Int): Flow<GetProfileDetailsPartialState>
}

class UserInteractorImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val repository: UserRepository,
    private val resourceProvider: ResourceProvider,
    private val gson: Gson,
    private val configController: ConfigController,
    private val authController: AuthController
) : UserInteractor {
    override suspend fun postSignUp(userSignupRequest: UserSignupRequest): Flow<PostSignUpPartialState> =
        flow {
            apiClient.postSignup(userSignupRequest).let { response ->
                if (response.isSuccessful) {
                    // Success.
                    response.body()?.let { userSignupResponse ->
                        emit(
                            PostSignUpPartialState.Success(
                                signUpResponse = userSignupResponse
                            )
                        )
                    } ?: run {
                        // Null body, thus server responded erroneously.
                        emit(
                            PostSignUpPartialState.Failed(
                                errorMessage = resourceProvider.getGenericErrorMessage()
                            )
                        )
                    }
                } else {
                    // Error.
                    val deserializedError =
                        gson.fromJson(
                            response.errorBody().toString(),
                            UserResponseError::class.java
                        )

                    emit(
                        PostSignUpPartialState.Failed(
                            errorMessage = deserializedError.errorMessage
                        )
                    )
                }
            }
        }.safeAsync {
            // Exception.
            when (it) {
                is IOException -> {
                    PostSignUpPartialState.Failed(
                        errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                    )
                }

                else -> {
                    PostSignUpPartialState.Failed(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun postLogin(userLoginRequest: UserLoginRequest): Flow<PostLoginPartialState> =
        flow {
            apiClient.postLogin(userLoginRequest).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { userLoginResponse ->
                        // Success.
                        emit(
                            PostLoginPartialState.Success(
                                userLoginResponseDomain = userLoginResponse.toUserLoginResponseDomain()
                            )
                        )
                    } ?: run {
                        // Null body, thus server responded erroneously.
                        emit(
                            PostLoginPartialState.Failed(
                                errorMessage = resourceProvider.getString(R.string.internal_server_error)
                            )
                        )
                    }
                } else {
                    // Error.
                    val deserializedError = gson.fromJson(
                        (response.errorBody() as ResponseBody).string(),
                        UserResponseError::class.java
                    )

                    emit(
                        PostLoginPartialState.Failed(
                            errorMessage = deserializedError.errorMessage
                        )
                    )
                }
            }
        }.safeAsync {
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

    override suspend fun postLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Flow<PostLoginWithTokenPartialState> =
        flow {
            apiClient.postLoginWithToken(userLoginWithTokenRequest).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { userLoginWithTokenResponse ->
                        emit(
                            PostLoginWithTokenPartialState.Success(
                                userLoginWithTokenResponseDomain = userLoginWithTokenResponse.toUserLoginWithTokenResponseDomain()
                            )
                        )
                    } ?: run {
                        emit(
                            PostLoginWithTokenPartialState.Failed(
                                errorMessage = resourceProvider.getString(R.string.internal_server_error)
                            )
                        )
                    }
                } else {
                    emit(
                        PostLoginWithTokenPartialState.Failed(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }.safeAsync {
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

    override suspend fun logout(): Flow<LogoutPartialState> = flow {
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()
        val clientId = configController.getClientId()
        val refreshToken = authController.getRefreshToken()

        if (refreshToken.isNullOrEmpty()) {
            emit(LogoutPartialState.Success)
            return@flow
        }

        repository.logout(clientId, refreshToken).collect { logoutRepositoryResponse ->
            // Remove user credentials irrespectively of the response.
            authController.removeUserCredentials()

            when (logoutRepositoryResponse) {
                is LogoutRepositoryResponse.Error -> {
                    emit(
                        LogoutPartialState.Failed(
                            errorMessage = genericErrorMessage
                        )
                    )
                }

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
                clientId = configController.getClientId(),
                clientSecret = configController.getClientSecret(),
                code = code,
                uri = configController.getUri()
            ).collect {
                when (it) {
                    is GrandNewAccessTokenRepositoryResponse.Error -> {
                        emit(
                            GrandNewAccessTokenPartialState.Failed(
                                errorMessage = it.errorMessage
                            )
                        )
                    }

                    is GrandNewAccessTokenRepositoryResponse.Failed -> {
                        emit(
                            GrandNewAccessTokenPartialState.Failed(
                                errorMessage = it.errorMessage ?: genericErrorMessage
                            )
                        )
                    }

                    is GrandNewAccessTokenRepositoryResponse.Success -> {
                        it.response?.let { response ->
                            // Save token info in device.
                            authController.setData(response.toUserAuthData())
                            emit(
                                GrandNewAccessTokenPartialState.Success(
                                    tokenResponse = response
                                )
                            )
                        } ?: emit(
                            GrandNewAccessTokenPartialState.Failed(
                                errorMessage = genericErrorMessage
                            )
                        )
                    }
                }
            }
        }

    override suspend fun refreshAccessToken(): Flow<RefreshAccessTokenPartialState> = flow {
        val clientId = configController.getClientId()
        val token = authController.getRefreshToken()
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()

        if (token.isNullOrBlank()) {
            emit(RefreshAccessTokenPartialState.Failed(errorMessage = genericErrorMessage))
            return@flow
        }

        repository.refreshAccessToken(clientId, token).collect {
            when (it) {
                is RefreshAccessTokenRepositoryResponse.Error -> {
                    // Remove stored credentials.
                    authController.removeUserCredentials()
                    emit(
                        RefreshAccessTokenPartialState.Failed(
                            errorMessage = genericErrorMessage
                        )
                    )
                }

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
                        emit(
                            RefreshAccessTokenPartialState.Success(
                                tokenResponse = tokenResponse
                            )
                        )
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
        when (it) {
            is IOException -> RefreshAccessTokenPartialState.Failed(
                errorMessage = resourceProvider.getNoInternetConnectionMessage()
            )

            else -> {
                logcat { "(refreshAccessToken#safeAsync#Error) ${it.localizedMessage.orEmpty()}" }
                RefreshAccessTokenPartialState.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
    }

    override suspend fun getProfileDetails(userId: Int): Flow<GetProfileDetailsPartialState> =
        flow {
            apiClient.getUserProfileDetails(userId = userId).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { userProfileDetailsResponse ->
                        emit(
                            GetProfileDetailsPartialState.Success(
                                profileDetails = userProfileDetailsResponse.toUserProfileDetailsDomain()
                            )
                        )
                    } ?: run {
                        emit(
                            GetProfileDetailsPartialState.Failed(
                                errorMessage = resourceProvider.getString(R.string.internal_server_error)
                            )
                        )
                    }
                } else {
                    emit(
                        GetProfileDetailsPartialState.Failed(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }.safeAsync {
            when (it) {
                is IOException -> GetProfileDetailsPartialState.NoInternetConnection(
                    errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                )

                else -> GetProfileDetailsPartialState.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
}

sealed class PostSignUpPartialState {
    data class Success(val signUpResponse: SignupResponse) : PostSignUpPartialState()

    data class Failed(val errorMessage: String) : PostSignUpPartialState()
}

sealed class PostLoginPartialState {
    data class Success(val userLoginResponseDomain: UserLoginResponseDomain) :
        PostLoginPartialState()

    data class Failed(val errorMessage: String) : PostLoginPartialState()
}

sealed class PostLoginWithTokenPartialState {
    data class Success(val userLoginWithTokenResponseDomain: UserLoginWithTokenResponseDomain) :
        PostLoginWithTokenPartialState()

    data class Failed(val errorMessage: String) : PostLoginWithTokenPartialState()
}

sealed class GetProfileDetailsPartialState {
    data class Success(val profileDetails: UserProfileDetailsDomain) :
        GetProfileDetailsPartialState()

    data class Failed(val errorMessage: String) : GetProfileDetailsPartialState()

    data class NoInternetConnection(val errorMessage: String) : GetProfileDetailsPartialState()
}

sealed class GrandNewAccessTokenPartialState {
    data class Success(val tokenResponse: TokenResponse) : GrandNewAccessTokenPartialState()
    data class Failed(val errorMessage: String) : GrandNewAccessTokenPartialState()
}

sealed class RefreshAccessTokenPartialState {
    data class Success(val tokenResponse: TokenResponse) : RefreshAccessTokenPartialState()
    data class Failed(val errorMessage: String) : RefreshAccessTokenPartialState()
}

sealed class LogoutPartialState {
    data object Success : LogoutPartialState()
    data class Failed(val errorMessage: String) : LogoutPartialState()
}