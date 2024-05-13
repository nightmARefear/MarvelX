package com.alexgreece45.marvelx.repository

import com.alexgreece45.marvelx.data.api.ApiClient
import kotlinx.coroutines.flow.Flow

interface ChangePasswordRepository {
    suspend fun updateUserPassword(
        requestIdEmail: String,
        requestIdPhoneNumber: String?,
        newPassword: String,
        newPasswordConfirmation: String
    ): Flow<ChangePasswordRepositoryUpdateUserPasswordResponse>
}

class ChangePasswordRepositoryImpl(
    private val apiClient: ApiClient
) : ChangePasswordRepository {
    override suspend fun updateUserPassword(
        requestIdEmail: String,
        requestIdPhoneNumber: String?,
        newPassword: String,
        newPasswordConfirmation: String
    ): Flow<ChangePasswordRepositoryUpdateUserPasswordResponse> {
        TODO("Not yet implemented")
    }
}

sealed interface ChangePasswordRepositoryUpdateUserPasswordResponse {
    data object Success : ChangePasswordRepositoryUpdateUserPasswordResponse
    data class Failed(val errorMessage: String) : ChangePasswordRepositoryUpdateUserPasswordResponse
    data class Error(val errorMessage: String) : ChangePasswordRepositoryUpdateUserPasswordResponse
}