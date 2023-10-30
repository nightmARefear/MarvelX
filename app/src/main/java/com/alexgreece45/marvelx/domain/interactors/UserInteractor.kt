package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.domains.user.login.UserLoginResponseDomain
import com.alexgreece45.marvelx.data.domains.user.loginWithToken.UserLoginWithTokenResponseDomain
import com.alexgreece45.marvelx.data.domains.user.profileDetails.UserProfileDetailsDomain
import com.alexgreece45.marvelx.data.models.token.TokenResponse
import com.alexgreece45.marvelx.data.models.user.UserResponseError
import com.alexgreece45.marvelx.data.models.user.login.UserLoginRequest
import com.alexgreece45.marvelx.data.models.user.login.toDomain
import com.alexgreece45.marvelx.data.models.user.loginWithToken.UserLoginWithTokenRequest
import com.alexgreece45.marvelx.data.models.user.loginWithToken.toUserLoginWithTokenResponseDomain
import com.alexgreece45.marvelx.data.models.user.profileDetails.toUserProfileDetailsDomain
import com.alexgreece45.marvelx.data.models.user.signUp.UserSignupRequest
import com.alexgreece45.marvelx.data.models.user.signUp.SignupResponse
import com.alexgreece45.marvelx.domain.repositories.GrantNewAccessTokenRepositoryResponse
import com.alexgreece45.marvelx.domain.repositories.UserRepository
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.safeAsync
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import okio.IOException
import javax.inject.Inject

interface UserInteractor {
    suspend fun postSignUp(userSignUpRequest: UserSignupRequest): Flow<PostSignUpPartialState>

    suspend fun postLogin(userLogInRequest: UserLoginRequest): Flow<PostLoginPartialState>

    suspend fun postLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Flow<PostLoginWithTokenPartialState>

    suspend fun grandNewAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        uri: String
    ): Flow<GrantNewAccessTokenPartialState>

    suspend fun getProfileDetails(userId: Int): Flow<GetProfileDetailsPartialState>
}

class UserInteractorImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val repository: UserRepository,
    private val resourceProvider: ResourceProvider,
    private val gson: Gson
) : UserInteractor {
    override suspend fun postSignUp(userSignUpRequest: UserSignupRequest): Flow<PostSignUpPartialState> =
        flow {
            apiClient.postSignup(userSignUpRequest).let { response ->
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

    override suspend fun postLogin(userLogInRequest: UserLoginRequest): Flow<PostLoginPartialState> =
        flow {
            apiClient.postLogin(userLogInRequest).let { response ->
                if (response.isSuccessful) {
                    response.body()?.let { userLoginResponse ->
                        // Success.
                        emit(
                            PostLoginPartialState.Success(
                                userLoginResponseDomain = userLoginResponse.toDomain()
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

    override suspend fun grandNewAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        uri: String
    ): Flow<GrantNewAccessTokenPartialState> = flow {
        val genericErrorMessage = resourceProvider.getGenericErrorMessage()
        repository.grantNewAccessToken(clientId, clientSecret, code, uri).collect {
            when (it) {
                is GrantNewAccessTokenRepositoryResponse.Error -> {
                    emit(
                        GrantNewAccessTokenPartialState.Failed(
                            errorMessage = it.errorMessage
                        )
                    )
                }

                is GrantNewAccessTokenRepositoryResponse.Failed -> {
                    emit(
                        GrantNewAccessTokenPartialState.Failed(
                            errorMessage = it.errorMessage ?: genericErrorMessage
                        )
                    )
                }

                is GrantNewAccessTokenRepositoryResponse.Success -> {
                    it.response?.let { response ->
                        emit(
                            GrantNewAccessTokenPartialState.Success(
                                tokenResponse = response
                            )
                        )
                    } ?: emit(
                        GrantNewAccessTokenPartialState.Failed(
                            errorMessage = genericErrorMessage
                        )
                    )
                }
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

sealed class GrantNewAccessTokenPartialState {
    data class Success(val tokenResponse: TokenResponse) : GrantNewAccessTokenPartialState()
    data class Failed(val errorMessage: String) : GrantNewAccessTokenPartialState()
}