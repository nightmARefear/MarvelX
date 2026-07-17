package com.alexgreece45.marvelx.domain.interactors

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Factory

interface ChangePasswordInteractor {
    suspend fun updateUserPassword(
        requestIdEmail: String,
        requestIdPhoneNumber: String?,
        newPassword: String,
        newPasswordConfirmation: String
    ): Flow<ChangePasswordUpdateUserPasswordPartialState>
}

@Factory
internal class ChangePasswordInteractorImpl : ChangePasswordInteractor {
    override suspend fun updateUserPassword(
        requestIdEmail: String,
        requestIdPhoneNumber: String?,
        newPassword: String,
        newPasswordConfirmation: String
    ): Flow<ChangePasswordUpdateUserPasswordPartialState> = flow {
        TODO("Not implemented yet.")
    }
}

sealed interface ChangePasswordUpdateUserPasswordPartialState {
    data object Success : ChangePasswordUpdateUserPasswordPartialState
    data class Failed(val errorMessage: String) : ChangePasswordUpdateUserPasswordPartialState
}