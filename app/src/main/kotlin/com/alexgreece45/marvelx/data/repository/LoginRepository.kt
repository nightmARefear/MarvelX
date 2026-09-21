package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.model.api.sensitiveUserData.RequestSensitiveUserData
import com.alexgreece45.marvelx.core.network.model.api.sensitiveUserData.ResponseSensitiveUserData
import com.alexgreece45.marvelx.core.network.model.api.token.ResponseToken
import com.alexgreece45.marvelx.data.repository.helper.parseUserError
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface LoginRepository {

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
     * Logouts the user.
     */
    suspend fun logout(clientId: String, refreshToken: String): RepositoryResult<String>

    /**
     * Post-login call. More specifically, signing data is retrieved which will be later used for
     * signing requests.
     */
    suspend fun postLogin(accessTokenAndType: String): Flow<PostLoginRepositoryResponse>

    suspend fun getSensitiveUserData(requestSensitiveUserData: RequestSensitiveUserData): Flow<GetSensitiveUserDataRepositoryResponse>
}

@Single
class LoginRepositoryImpl(
    private val logger: AppLogger,
    private val resourceProvider: ResourceProvider,
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) : LoginRepository {

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
                    val parsedError = parseUserError(response)
                    logger.e { "(grandNewAccessToken) failed: ${parsedError.errorMessage}" }
                    emit(
                        GrandNewAccessTokenRepositoryResponse.Failed(
                            errorMessage = resourceProvider.getGenericErrorMessage()
                        )
                    )
                }
            }
        }

    override suspend fun logout(
        clientId: String,
        refreshToken: String
    ): RepositoryResult<String> = handleApiResultNoTransformationNoFlow(
        apiCall = { apiClient.logout(clientId, refreshToken) },
        ioDispatcher = ioDispatcher
    )

    override suspend fun postLogin(accessTokenAndType: String): Flow<PostLoginRepositoryResponse> = flow {
        apiClient.postLogin(accessTokenAndType).let { response ->
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
                            responseUserError = parseUserError(response)
                        )
                    )
                }
            }
        }
}

sealed interface PostLoginRepositoryResponse {
    data class Success(val response: com.alexgreece45.marvelx.core.network.model.api.user.postLogin.ResponsePostLogin?) :
        PostLoginRepositoryResponse

    data class Failed(val errorMessage: String) : PostLoginRepositoryResponse
}

sealed class GrandNewAccessTokenRepositoryResponse {
    data class Success(val response: ResponseToken?) : GrandNewAccessTokenRepositoryResponse()
    data class Failed(val errorMessage: String) : GrandNewAccessTokenRepositoryResponse()
}

sealed class GetSensitiveUserDataRepositoryResponse {
    data class Success(val response: ResponseSensitiveUserData?) :
        GetSensitiveUserDataRepositoryResponse()

    data class Failed(val responseUserError: com.alexgreece45.marvelx.core.network.model.api.user.ResponseUserError) :
        GetSensitiveUserDataRepositoryResponse()
}