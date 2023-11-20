package com.alexgreece45.marvelx.repository

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.sensitiveUserData.RequestSensitiveUserData
import com.alexgreece45.marvelx.data.models.sensitiveUserData.ResponseSensitiveUserData
import com.alexgreece45.marvelx.data.models.token.TokenResponse
import com.alexgreece45.marvelx.data.models.user.ResponseUserError
import com.alexgreece45.marvelx.data.models.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.helper.parseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import logcat.LogPriority
import logcat.logcat
import retrofit2.Response
import javax.inject.Inject

interface UserRepository {
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
     * Gets the profile details of the given [userId].
     */
    suspend fun getProfileDetails(userId: Int): Flow<GetProfileDetailsRepositoryResponse>

    /**
     * Logouts the user.
     */
    suspend fun logout(clientId: String, refreshToken: String): Flow<LogoutRepositoryResponse>

    suspend fun getSensitiveUserData(requestSensitiveUserData: RequestSensitiveUserData): Flow<GetSensitiveUserDataRepositoryResponse>
}

class UserRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider,
) : UserRepository {
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

sealed class GetSensitiveUserDataRepositoryResponse {
    data class Success(val response: ResponseSensitiveUserData?) :
        GetSensitiveUserDataRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        GetSensitiveUserDataRepositoryResponse()
}