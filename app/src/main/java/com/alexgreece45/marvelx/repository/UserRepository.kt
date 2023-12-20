package com.alexgreece45.marvelx.repository

import com.alexgreece45.marvelx.data.api.ApiClient
import com.alexgreece45.marvelx.data.models.user.ResponseUserError
import com.alexgreece45.marvelx.data.models.user.profileDetails.ResponseProfileDetails
import com.alexgreece45.marvelx.util.helper.parseError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface UserRepository {
    /**
     * Gets the profile details of the given [userId].
     */
    suspend fun getProfileDetails(userId: Int): Flow<GetProfileDetailsRepositoryResponse>
}

class UserRepositoryImpl @Inject constructor(
    private val apiClient: ApiClient
) : UserRepository {
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
}

sealed class GetProfileDetailsRepositoryResponse {
    data class Success(val response: ResponseProfileDetails?) :
        GetProfileDetailsRepositoryResponse()

    data class Failed(val responseUserError: ResponseUserError) :
        GetProfileDetailsRepositoryResponse()
}