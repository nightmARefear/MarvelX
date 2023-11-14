package com.alexgreece45.marvelx.repository

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.token.TokenResponse
import com.alexgreece45.marvelx.data.models.user.ResponseUserError
import com.alexgreece45.marvelx.data.models.user.login.LoginResponse
import com.alexgreece45.marvelx.data.models.user.login.UserLoginRequest
import com.alexgreece45.marvelx.data.models.user.loginWithToken.LoginWithTokenResponse
import com.alexgreece45.marvelx.data.models.user.loginWithToken.UserLoginWithTokenRequest
import com.alexgreece45.marvelx.data.models.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.data.models.user.signup.SignupResponse
import com.alexgreece45.marvelx.data.models.user.signup.UserSignupRequest
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.helper.parseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import logcat.LogPriority
import logcat.logcat
import retrofit2.Response
import javax.inject.Inject

interface UserRepository {
    suspend fun postLogin(userLoginRequest: UserLoginRequest): Flow<PostLoginRepositoryResponse>
    suspend fun postLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Flow<PostLoginWithTokenRepositoryResponse>
    suspend fun postSignup(userSignupRequest: UserSignupRequest): Flow<PostSignupRepositoryResponse>
    suspend fun grandNewAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        uri: String
    ): Flow<GrandNewAccessTokenRepositoryResponse>

    suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Flow<RefreshAccessTokenRepositoryResponse>

    suspend fun getProfileDetails(userId: Int): Flow<GetProfileDetailsRepositoryResponse>

    suspend fun logout(clientId: String, refreshToken: String): Flow<LogoutRepositoryResponse>
}

class UserRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider,
) : UserRepository {
    override suspend fun postLogin(userLoginRequest: UserLoginRequest): Flow<PostLoginRepositoryResponse> =
        flow {
            apiClient.postLogin(userLoginRequest).let { response ->
                if (response.isSuccessful) {
                    emit(
                        PostLoginRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        PostLoginRepositoryResponse.Failed(
                            responseUserError = parseError(response)
                        )
                    )
                }
            }
        }

    override suspend fun postLoginWithToken(userLoginWithTokenRequest: UserLoginWithTokenRequest): Flow<PostLoginWithTokenRepositoryResponse> =
        flow {
            apiClient.postLoginWithToken(userLoginWithTokenRequest).let { response ->
                if (response.isSuccessful) {
                    emit(
                        PostLoginWithTokenRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        PostLoginWithTokenRepositoryResponse.Failed(
                            responseUserError = parseError(
                                response
                            )
                        )
                    )
                }
            }
        }

    override suspend fun postSignup(userSignupRequest: UserSignupRequest): Flow<PostSignupRepositoryResponse> =
        flow {
            apiClient.postSignup(userSignupRequest).let { response ->
                if (response.isSuccessful) {
                    emit(
                        PostSignupRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        PostSignupRepositoryResponse.Failed(
                            responseUserError = parseError(response)
                        )
                    )
                }
            }
        }

    override suspend fun grandNewAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        uri: String
    ): Flow<GrandNewAccessTokenRepositoryResponse> =
        flow {
            apiClient.grantNewAccessToken(
                clientId = clientId,
                clientSecret = clientSecret,
                code = code,
                uri = uri
            ).let { response ->
                if (response.isSuccessful) {
                    emit(GrandNewAccessTokenRepositoryResponse.Success(response = response.body()))
                } else {
                    val parsedError = parseError(response)
                    logcat(LogPriority.ERROR) { "(grandNewAccessToken) failed: ${parsedError.errorMessage}" }
                    emit(
                        GrandNewAccessTokenRepositoryResponse.Failed(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }

    override suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Flow<RefreshAccessTokenRepositoryResponse> = flow {
        val response = apiClient.refreshAccessToken(
            clientId = clientId,
            refreshToken = refreshToken
        )

        if (response.isSuccessful) {
            emit(
                RefreshAccessTokenRepositoryResponse.Success(
                    response = response.body()
                )
            )
        } else {
            emit(
                RefreshAccessTokenRepositoryResponse.Failed(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            )
        }
    }

    override suspend fun getProfileDetails(userId: Int): Flow<GetProfileDetailsRepositoryResponse> =
        flow {
            apiClient.getUserProfileDetails(userId).let { response ->
                if (response.isSuccessful) {
                    emit(
                        GetProfileDetailsRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        GetProfileDetailsRepositoryResponse.Failed(
                            responseUserError = parseError(
                                response
                            )
                        )
                    )
                }
            }
        }

    override suspend fun logout(
        clientId: String,
        refreshToken: String
    ): Flow<LogoutRepositoryResponse> = flow {
        apiClient.logout(clientId, refreshToken).let { response ->
            if (response.isSuccessful) {
                emit(LogoutRepositoryResponse.Success(response))
            } else {
                emit(LogoutRepositoryResponse.Failed(errorMessage = resourceProvider.getGenericErrorMessage()))
            }
        }
    }
}

sealed class PostLoginRepositoryResponse {
    data class Success(val response: LoginResponse?) : PostLoginRepositoryResponse()
    data class Failed(val responseUserError: ResponseUserError) : PostLoginRepositoryResponse()
}

sealed class PostLoginWithTokenRepositoryResponse {
    data class Success(val response: LoginWithTokenResponse?) :
        PostLoginWithTokenRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        PostLoginWithTokenRepositoryResponse()
}

sealed class PostSignupRepositoryResponse {
    data class Success(val response: SignupResponse?) : PostSignupRepositoryResponse()
    data class Failed(val responseUserError: ResponseUserError) : PostSignupRepositoryResponse()
}

sealed class GrandNewAccessTokenRepositoryResponse {
    data class Success(val response: TokenResponse?) : GrandNewAccessTokenRepositoryResponse()
    data class Failed(val errorMessage: String) : GrandNewAccessTokenRepositoryResponse()
}

sealed class RefreshAccessTokenRepositoryResponse {
    data class Success(val response: TokenResponse?) : RefreshAccessTokenRepositoryResponse()
    data class Failed(val errorMessage: String?) : RefreshAccessTokenRepositoryResponse()
}

sealed class GetProfileDetailsRepositoryResponse {
    data class Success(val response: ResponseProfileDetails?) :
        GetProfileDetailsRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        GetProfileDetailsRepositoryResponse()
}

sealed class LogoutRepositoryResponse {
    data class Success(val response: Response<Void>) : LogoutRepositoryResponse()
    data class Failed(val errorMessage: String?) : LogoutRepositoryResponse()
}