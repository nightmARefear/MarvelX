package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.data.repository.helper.parseError
import com.alexgreece45.marvelx.network_logic.api.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.authorize.ResponseAuthorize
import com.alexgreece45.marvelx.network_logic.models_api.sensitiveUserData.RequestSensitiveUserData
import com.alexgreece45.marvelx.network_logic.models_api.sensitiveUserData.ResponseSensitiveUserData
import com.alexgreece45.marvelx.network_logic.models_api.token.ResponseToken
import com.alexgreece45.marvelx.network_logic.models_api.user.ResponseUserError
import com.alexgreece45.marvelx.network_logic.models_api.user.postLogin.ResponsePostLogin
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

interface LoginRepository {
    suspend fun authorize(
        tempAuthCode: String,
        clientId: String,
        codeChallenge: String,
        codeChallengeMethod: String
    ): Flow<AuthorizeRepositoryPartialState>

    /**
     * Grands an access token based on the given [code] that has been retrieved by the server to
     * uniquely identify this user, and [codeVerifier] that is the plain text version of the `code
     * challenge`.
     */
    suspend fun grandNewAccessToken(
        clientId: String,
        code: String,
        codeVerifier: String,
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
    suspend fun postLogin(accessToken: String): Flow<PostLoginRepositoryResponse>

    suspend fun getSensitiveUserData(requestSensitiveUserData: RequestSensitiveUserData): Flow<GetSensitiveUserDataRepositoryResponse>
}

class LoginRepositoryImpl(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider,
    private val logger: AppLogger
) : LoginRepository {
    override suspend fun authorize(
        tempAuthCode: String,
        clientId: String,
        codeChallenge: String,
        codeChallengeMethod: String
    ): Flow<AuthorizeRepositoryPartialState> = flow {
        apiClient.authorize(tempAuthCode, clientId, codeChallenge, codeChallengeMethod)
            .let { response ->
                if (response.isSuccessful) {
                    emit(AuthorizeRepositoryPartialState.Success(response.body()))
                } else {
                    emit(AuthorizeRepositoryPartialState.Failed(resourceProvider.getGenericErrorMessage()))
                }
            }
    }

    override suspend fun grandNewAccessToken(
        clientId: String,
        code: String,
        codeVerifier: String,
        uri: String
    ): Flow<GrandNewAccessTokenRepositoryResponse> =
        flow {
            apiClient.grandNewAccessToken(
                clientId = clientId,
                codeVerifier = codeVerifier,
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
            emit(RefreshAccessTokenRepositoryResponse.Success(response = response.body()))
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

    override suspend fun postLogin(accessToken: String): Flow<PostLoginRepositoryResponse> = flow {
        apiClient.postLogin(accessToken).let { response ->
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

sealed interface AuthorizeRepositoryPartialState {
    data class Success(val response: ResponseAuthorize?) : AuthorizeRepositoryPartialState
    data class Failed(val errorMessage: String) : AuthorizeRepositoryPartialState
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