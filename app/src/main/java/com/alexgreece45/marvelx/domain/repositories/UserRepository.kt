package com.alexgreece45.marvelx.domain.repositories

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.token.TokenResponse
import com.alexgreece45.marvelx.domain.controllers.ConfigController
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface UserRepository {
    suspend fun grantNewAccessToken(code: String): Flow<GrantNewAccessTokenRepositoryResponse>

    suspend fun refreshAccessToken(refreshToken: String): Flow<RefreshAccessTokenRepositoryResponse>
}

class UserRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider,
    private val configController: ConfigController
) : UserRepository {
    override suspend fun grantNewAccessToken(code: String): Flow<GrantNewAccessTokenRepositoryResponse> =
        flow {
            val response = apiClient.grandNewAccessToken(
                clientId = configController.getClientId(),
                clientSecret = configController.getClientSecret(),
                code = code,
                uri = configController.getUri()
            )
            if (response.isSuccessful) {
                emit(GrantNewAccessTokenRepositoryResponse.Success(response = response.body()))
            } else {
                emit(GrantNewAccessTokenRepositoryResponse.Failed(errorMessage = resourceProvider.getGenericErrorMessage()))
            }
        }.catch {
            emit(
                GrantNewAccessTokenRepositoryResponse.Failed(
                    errorMessage = it.localizedMessage
                )
            )
        }

    override suspend fun refreshAccessToken(refreshToken: String): Flow<RefreshAccessTokenRepositoryResponse> {
        TODO("Not yet implemented")
    }
}

sealed class GrantNewAccessTokenRepositoryResponse {
    data class Success(val response: TokenResponse?) : GrantNewAccessTokenRepositoryResponse()
    data class Failed(val errorMessage: String?) : GrantNewAccessTokenRepositoryResponse()
    data class Error(val errorMessage: String) : GrantNewAccessTokenRepositoryResponse()
}

sealed class RefreshAccessTokenRepositoryResponse {
    data class Success(val response: TokenResponse?) : RefreshAccessTokenRepositoryResponse()
    data class Failed(val errorMessage: String?) : RefreshAccessTokenRepositoryResponse()
    data class Error(val errorMessage: String) : RefreshAccessTokenRepositoryResponse()
}