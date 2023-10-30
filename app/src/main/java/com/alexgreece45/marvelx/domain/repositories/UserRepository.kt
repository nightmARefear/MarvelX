package com.alexgreece45.marvelx.domain.repositories

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.token.TokenResponse
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface UserRepository {
    suspend fun grantNewAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        uri: String
    ): Flow<GrantNewAccessTokenRepositoryResponse>
}

class UserRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient,
    private val resourceProvider: ResourceProvider
) : UserRepository {
    override suspend fun grantNewAccessToken(
        clientId: String,
        clientSecret: String,
        code: String,
        uri: String
    ): Flow<GrantNewAccessTokenRepositoryResponse> = flow {
        val response = apiClient.grandNewAccessToken(clientId, clientSecret, code, uri)
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
}

sealed class GrantNewAccessTokenRepositoryResponse {
    data class Success(val response: TokenResponse?) : GrantNewAccessTokenRepositoryResponse()
    data class Error(val errorMessage: String) : GrantNewAccessTokenRepositoryResponse()
    data class Failed(val errorMessage: String?) : GrantNewAccessTokenRepositoryResponse()
}