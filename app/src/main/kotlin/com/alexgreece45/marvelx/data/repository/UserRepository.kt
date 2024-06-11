package com.alexgreece45.marvelx.data.repository

import com.alexgreece45.marvelx.data.repository.helper.parseError
import com.alexgreece45.marvelx.network_logic.api.ApiClient
import com.alexgreece45.marvelx.network_logic.models_api.user.ResponseUserError
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.RequestProfileDetailsUpdate
import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseProfileDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface UserRepository {
    /**
     * Gets the profile details of the given [userId].
     */
    suspend fun getProfileDetails(userId: Int): Flow<GetProfileDetailsRepositoryResponse>

    /**
     * Get the profile details of the current user.
     */
    suspend fun getCurrentUserProfileDetails(): Flow<GetProfileDetailsRepositoryResponse>

    /**
     * Updates the profile details of the current user.
     */
    suspend fun updateCurrentUserProfileDetails(
        firstName: String,
        lastName: String,
        gender: String,
        email: String
    ): Flow<UpdateProfileDetailsRepositoryPartialState>
}

class UserRepositoryImpl(
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

    override suspend fun getCurrentUserProfileDetails(): Flow<GetProfileDetailsRepositoryResponse> =
        flow {
            apiClient.getUserMe().let { response ->
                if (response.isSuccessful) {
                    emit(GetProfileDetailsRepositoryResponse.Success(response.body()))
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

    override suspend fun updateCurrentUserProfileDetails(
        firstName: String,
        lastName: String,
        gender: String,
        email: String
    ): Flow<UpdateProfileDetailsRepositoryPartialState> = flow {
        val request = RequestProfileDetailsUpdate(
            firstName = firstName,
            lastName = lastName,
            gender = gender,
            email = email
        )
        apiClient.updateUserMe(request).let { response ->
            if (response.isSuccessful) {
                emit(UpdateProfileDetailsRepositoryPartialState.Success)
            } else {
                emit(UpdateProfileDetailsRepositoryPartialState.Failed)
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

sealed interface UpdateProfileDetailsRepositoryPartialState {
    data object Success : UpdateProfileDetailsRepositoryPartialState
    data object Failed : UpdateProfileDetailsRepositoryPartialState
}