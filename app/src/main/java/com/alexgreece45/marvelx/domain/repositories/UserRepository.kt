package com.alexgreece45.marvelx.domain.repositories

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.token.TokenResponse
import com.alexgreece45.marvelx.data.models.user.UserResponseError
import com.alexgreece45.marvelx.data.models.user.signup.SignupResponse
import com.alexgreece45.marvelx.data.models.user.signup.UserSignupRequest
import com.alexgreece45.marvelx.util.ResourceProvider
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import retrofit2.Response
import javax.inject.Inject

interface UserRepository {
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

    suspend fun logout(clientId: String, refreshToken: String): Flow<LogoutRepositoryResponse>
}

class UserRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider,
) : UserRepository {
    override suspend fun postSignup(userSignupRequest: UserSignupRequest): Flow<PostSignupRepositoryResponse> =
        flow {
            val response = apiClient.postSignup(userSignupRequest)
            if (response.isSuccessful) {
                emit(
                    PostSignupRepositoryResponse.Success(
                        response = response.body()
                    )
                )
            } else {
                val deserializedError =
                    Gson().fromJson(
                        response.errorBody()?.string(),
                        UserResponseError::class.java
                    )

                emit(
                    PostSignupRepositoryResponse.Failed(
                        userResponseError = deserializedError
                    )
                )
            }
        }.catch {
            emit(
                PostSignupRepositoryResponse.Error(
                    errorMessage = it.message
                )
            )
        }

    override suspend fun grandNewAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        uri: String
    ): Flow<GrandNewAccessTokenRepositoryResponse> =
        flow {
            val response = apiClient.grantNewAccessToken(
                clientId = clientId,
                clientSecret = clientSecret,
                code = code,
                uri = uri
            )
            if (response.isSuccessful) {
                emit(GrandNewAccessTokenRepositoryResponse.Success(response = response.body()))
            } else {
                emit(GrandNewAccessTokenRepositoryResponse.Failed(errorMessage = resourceProvider.getGenericErrorMessage()))
            }
        }.catch {
            emit(
                GrandNewAccessTokenRepositoryResponse.Failed(
                    errorMessage = it.localizedMessage
                )
            )
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
                RefreshAccessTokenRepositoryResponse.Error(
                    errorMessage = resourceProvider.getGenericErrorMessage()
                )
            )
        }
    }.catch {
        emit(
            RefreshAccessTokenRepositoryResponse.Failed(
                errorMessage = it.localizedMessage
            )
        )
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
    }.catch {
        emit(
            LogoutRepositoryResponse.Failed(
                errorMessage = it.localizedMessage
            )
        )
    }
}

sealed class GrandNewAccessTokenRepositoryResponse {
    data class Success(val response: TokenResponse?) : GrandNewAccessTokenRepositoryResponse()
    data class Failed(val errorMessage: String?) : GrandNewAccessTokenRepositoryResponse()
    data class Error(val errorMessage: String) : GrandNewAccessTokenRepositoryResponse()
}

sealed class RefreshAccessTokenRepositoryResponse {
    data class Success(val response: TokenResponse?) : RefreshAccessTokenRepositoryResponse()
    data class Failed(val errorMessage: String?) : RefreshAccessTokenRepositoryResponse()
    data class Error(val errorMessage: String) : RefreshAccessTokenRepositoryResponse()
}

sealed class LogoutRepositoryResponse {
    data class Success(val response: Response<Void>) : LogoutRepositoryResponse()
    data class Failed(val errorMessage: String?) : LogoutRepositoryResponse()
    data class Error(val errorMessage: String) : LogoutRepositoryResponse()
}

sealed class PostSignupRepositoryResponse {
    data class Success(val response: SignupResponse?) : PostSignupRepositoryResponse()
    data class Failed(val userResponseError: UserResponseError) : PostSignupRepositoryResponse()
    data class Error(val errorMessage: String?) : PostSignupRepositoryResponse()
}