package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.domains.user.login.UserLoginResponseDomain
import com.alexgreece45.marvelx.data.domains.user.loginWithToken.UserLoginWithTokenResponseDomain
import com.alexgreece45.marvelx.data.domains.user.profileDetails.UserProfileDetailsDomain
import com.alexgreece45.marvelx.data.models.user.UserResponseError
import com.alexgreece45.marvelx.data.models.user.login.UserLoginRequest
import com.alexgreece45.marvelx.data.models.user.login.toDomain
import com.alexgreece45.marvelx.data.models.user.loginWithToken.UserLoginWithTokenRequest
import com.alexgreece45.marvelx.data.models.user.loginWithToken.toUserLoginWithTokenResponseDomain
import com.alexgreece45.marvelx.data.models.user.profileDetails.toUserProfileDetailsDomain
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignupRequest
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignupResponse
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import okio.IOException
import javax.inject.Inject

interface UserInteractor {
    suspend fun postSignUp(userSignUpRequest: UserSignupRequest): Flow<UserPartialStatePostSignup>

    suspend fun postLogin(userLogInRequest: UserLoginRequest): Flow<UserPartialStatePostLogin>

    suspend fun postLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Flow<UserPartialStatePostLoginWithToken>

    suspend fun getProfileDetails(userId: Int): Flow<UserPartialStateGetProfileDetails>
}

class UserInteractorImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider,
    private val gson: Gson
) : UserInteractor {
    override suspend fun postSignUp(userSignUpRequest: UserSignupRequest): Flow<UserPartialStatePostSignup> =
        flow {
            apiClient.postUserSignup(userSignUpRequest).let { response ->
                if (response.isSuccessful) {
                    // Success.
                    response.body()?.let { userSignupResponse ->
                        emit(
                            UserPartialStatePostSignup.Success(
                                userSignUpResponse = userSignupResponse
                            )
                        )
                    } ?: run {
                        // Null body, thus server responded erroneously.
                        emit(
                            UserPartialStatePostSignup.Error(
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
                        UserPartialStatePostSignup.Error(
                            errorMessage = deserializedError.errorMessage
                        )
                    )
                }
            }
        }.safeAsync {
            // Exception.
            when (it) {
                is IOException -> {
                    UserPartialStatePostSignup.Error(
                        errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                    )
                }

                else -> {
                    UserPartialStatePostSignup.Error(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun postLogin(userLogInRequest: UserLoginRequest): Flow<UserPartialStatePostLogin> =
        flow {
            apiClient.postUserLogin(userLogInRequest).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { userLoginResponse ->
                        // Success.
                        emit(
                            UserPartialStatePostLogin.Success(
                                userLoginResponseDomain = userLoginResponse.toDomain()
                            )
                        )
                    } ?: run {
                        // Null body, thus server responded erroneously.
                        emit(
                            UserPartialStatePostLogin.Error(
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
                        UserPartialStatePostLogin.Error(
                            errorMessage = deserializedError.errorMessage
                        )
                    )
                }
            }
        }.safeAsync {
            when (it) {
                is IOException -> {
                    UserPartialStatePostLogin.Error(
                        errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                    )
                }

                else -> {
                    UserPartialStatePostLogin.Error(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun postLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Flow<UserPartialStatePostLoginWithToken> =
        flow {
            apiClient.postUserLoginWithToken(userLoginWithTokenRequest).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { userLoginWithTokenResponse ->
                        emit(
                            UserPartialStatePostLoginWithToken.Success(
                                userLoginWithTokenResponseDomain = userLoginWithTokenResponse.toUserLoginWithTokenResponseDomain()
                            )
                        )
                    } ?: run {
                        emit(
                            UserPartialStatePostLoginWithToken.Error(
                                errorMessage = resourceProvider.getString(R.string.internal_server_error)
                            )
                        )
                    }
                } else {
                    emit(
                        UserPartialStatePostLoginWithToken.Error(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }.safeAsync {
            when (it) {
                is IOException -> {
                    UserPartialStatePostLoginWithToken.Error(
                        errorMessage = resourceProvider.getNoInternetConnectionMessage()
                    )
                }

                else -> {
                    UserPartialStatePostLoginWithToken.Error(
                        errorMessage = resourceProvider.getGenericErrorMessage()
                    )
                }
            }
        }

    override suspend fun getProfileDetails(userId: Int): Flow<UserPartialStateGetProfileDetails> =
        flow {
            apiClient.getUserProfileDetails(userId = userId).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { userProfileDetailsResponse ->
                        emit(
                            UserPartialStateGetProfileDetails.Success(
                                profileDetails = userProfileDetailsResponse.toUserProfileDetailsDomain()
                            )
                        )
                    } ?: run {
                        emit(
                            UserPartialStateGetProfileDetails.Error(
                                errorMessage = resourceProvider.getString(R.string.internal_server_error)
                            )
                        )
                    }
                } else {
                    emit(
                        UserPartialStateGetProfileDetails.Error(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }.safeAsync {
            when (it) {
                is IOException -> UserPartialStateGetProfileDetails.NoInternetConnection(
                    errorMessage = resourceProvider.getString(R.string.no_internet_connection_error_message)
                )

                else -> UserPartialStateGetProfileDetails.Error(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            }
        }
}

sealed class UserPartialStatePostSignup {
    data class Success(val userSignUpResponse: UserSignupResponse) : UserPartialStatePostSignup()

    data class Error(val errorMessage: String) : UserPartialStatePostSignup()
}

sealed class UserPartialStatePostLogin {
    data class Success(val userLoginResponseDomain: UserLoginResponseDomain) :
        UserPartialStatePostLogin()

    data class Error(val errorMessage: String) : UserPartialStatePostLogin()
}

sealed class UserPartialStatePostLoginWithToken {
    data class Success(val userLoginWithTokenResponseDomain: UserLoginWithTokenResponseDomain) :
        UserPartialStatePostLoginWithToken()

    data class Error(val errorMessage: String) : UserPartialStatePostLoginWithToken()
}

sealed class UserPartialStateGetProfileDetails {
    data class Success(val profileDetails: UserProfileDetailsDomain) :
        UserPartialStateGetProfileDetails()

    data class Error(val errorMessage: String) : UserPartialStateGetProfileDetails()

    data class NoInternetConnection(val errorMessage: String) : UserPartialStateGetProfileDetails()
}