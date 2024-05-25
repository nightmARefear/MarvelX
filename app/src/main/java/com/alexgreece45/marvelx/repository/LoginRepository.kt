package com.alexgreece45.marvelx.repository

import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models_api.sensitiveUserData.RequestSensitiveUserData
import com.alexgreece45.marvelx.data.models_api.sensitiveUserData.ResponseSensitiveUserData
import com.alexgreece45.marvelx.data.models_api.token.ResponseToken
import com.alexgreece45.marvelx.data.models_api.user.ResponseUserError
import com.alexgreece45.marvelx.data.models_api.user.postLogin.ResponsePostLogin
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.helper.parseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

interface LoginRepository {
    /**
     * Grands a new access token upon login or signup.
     */
    suspend fun grandNewAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        uri: String
    ): Flow<GrandNewAccessTokenRepositoryResponse>

    /**
     * Refreshes the access token.
     */
    suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Flow<RefreshAccessTokenRepositoryResponse>

    /**
     * Logouts the user.
     */
    suspend fun logout(clientId: String, refreshToken: String): Flow<LogoutRepositoryResponse>

    /**
     * Post-login call. More specifically, signing data is retrieved which will be later used for
     * signing requests.
     */
    suspend fun postLogin(): Flow<PostLoginRepositoryResponse>

    suspend fun getSensitiveUserData(requestSensitiveUserData: RequestSensitiveUserData): Flow<GetSensitiveUserDataRepositoryResponse>
}

class LoginRepositoryImpl(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider,
    private val logger: AppLoggerController
) : LoginRepository {
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
                    logger.e { "(grandNewAccessToken) failed: ${parsedError.errorMessage}" }
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

    override suspend fun postLogin(): Flow<PostLoginRepositoryResponse> = flow {
        apiClient.postLogin().let { response ->
            if (response.isSuccessful) {
                emit(PostLoginRepositoryResponse.Success(response.body()))
            } else {
                emit(PostLoginRepositoryResponse.Failed(errorMessage = resourceProvider.getGenericErrorMessage()))
            }
        }
    }

    override suspend fun getSensitiveUserData(requestSensitiveUserData: RequestSensitiveUserData): Flow<GetSensitiveUserDataRepositoryResponse> =
        flow {
            apiClient.getSensitiveUserData(requestSensitiveUserData).let { response ->
                if (response.isSuccessful) {
                    emit(
                        GetSensitiveUserDataRepositoryResponse.Success(
                            response = response.body()
                        )
                    )
                } else {
                    emit(
                        GetSensitiveUserDataRepositoryResponse.Failed(
                            responseUserError = parseError(response)
                        )
                    )
                }
            }
        }
}

sealed interface PostLoginRepositoryResponse {
    data class Success(val response: ResponsePostLogin?) : PostLoginRepositoryResponse
    data class Failed(val errorMessage: String) : PostLoginRepositoryResponse
}

sealed class GrandNewAccessTokenRepositoryResponse {
    data class Success(val response: ResponseToken?) : GrandNewAccessTokenRepositoryResponse()
    data class Failed(val errorMessage: String) : GrandNewAccessTokenRepositoryResponse()
}

sealed class RefreshAccessTokenRepositoryResponse {
    data class Success(val response: ResponseToken?) : RefreshAccessTokenRepositoryResponse()
    data class Failed(val errorMessage: String?) : RefreshAccessTokenRepositoryResponse()
}

sealed class LogoutRepositoryResponse {
    data class Success(val response: Response<Void>) : LogoutRepositoryResponse()
    data class Failed(val errorMessage: String?) : LogoutRepositoryResponse()
}

sealed class GetSensitiveUserDataRepositoryResponse {
    data class Success(val response: ResponseSensitiveUserData?) :
        GetSensitiveUserDataRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        GetSensitiveUserDataRepositoryResponse()
}