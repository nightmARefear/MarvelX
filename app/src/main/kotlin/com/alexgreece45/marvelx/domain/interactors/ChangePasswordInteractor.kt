package com.alexgreece45.marvelx.domain.interactors

import com.alexgreece45.marvelx.core.common.dispatchers.model.DefaultDispatcher
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import com.alexgreece45.marvelx.data.repository.ChangePasswordRepository
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep
import com.alexgreece45.marvelx.util.SecurityUtil
import kotlinx.coroutines.flow.single
import org.koin.core.annotation.Factory
import kotlin.coroutines.CoroutineContext

interface ChangePasswordInteractor {
    suspend fun resetPassword(requestId: String, newPassword: String): ChangePasswordPartialState
}

sealed interface ChangePasswordPartialState {
    data object Success : ChangePasswordPartialState
    data class Failed(val errorMessage: String) : ChangePasswordPartialState
    data object NoInternetConnection : ChangePasswordPartialState
}

@Factory
internal class ChangePasswordInteractorImpl(
    private val changePasswordRepository: ChangePasswordRepository,
    @DefaultDispatcher private val defaultDispatcher: CoroutineContext
) : ChangePasswordInteractor {

    override suspend fun resetPassword(requestId: String, newPassword: String): ChangePasswordPartialState {
        // Same client-side hash as signup and login, otherwise the next login would not match.
        val hashedPassword = SecurityUtil.getHashedPasswordFromString(
            defaultDispatcher = defaultDispatcher,
            password = newPassword
        ).single()

        return when (
            val result = changePasswordRepository.resetPassword(
                requestId = requestId,
                verificationStep = VerificationStep.OtpEmail.key,
                hashedPassword = hashedPassword
            )
        ) {
            is RepositoryResult.Success -> ChangePasswordPartialState.Success
            is RepositoryResult.Failed -> ChangePasswordPartialState.Failed(result.failureMessage)
            RepositoryResult.NetworkError -> ChangePasswordPartialState.NoInternetConnection
        }
    }
}
