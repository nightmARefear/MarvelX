package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.core.data.helper.handleApiResultNoTransformationNoFlow
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.util.ResourceProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

interface LoginRepository {

    /**
     * Logouts the user.
     */
    suspend fun logout(clientId: String, refreshToken: String): RepositoryResult<String>

    /**
     * Post-login call. More specifically, signing data is retrieved which will be later used for
     * signing requests.
     */
    suspend fun postLogin(accessTokenAndType: String): Flow<PostLoginRepositoryResponse>
}

@Single
class LoginRepositoryImpl(
    private val resourceProvider: ResourceProvider,
    private val apiClient: ApiClient,
    @IoDispatcher private val ioDispatcher: CoroutineContext,
) : LoginRepository {

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
}

sealed interface PostLoginRepositoryResponse {
    data class Success(val response: com.alexgreece45.marvelx.core.network.model.api.user.postLogin.ResponsePostLogin?) :
        PostLoginRepositoryResponse

    data class Failed(val errorMessage: String) : PostLoginRepositoryResponse
}
