package com.alexgreece45.marvelx.domain.usecases

import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.core.network.storage.SignatureStorage
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.annotation.Factory
import kotlin.coroutines.CoroutineContext

/**
 * Removes the stored user credentials: user ID, authentication tokens, sign data.
 *
 * ### Note: This operation is irreversible, and it MUST be used with caution.
 * */
@Factory
class RemoveUserCredentialsUseCase(
    private val userSessionRepository: UserSessionRepository,
    private val signatureStorage: SignatureStorage,
    @IoDispatcher private val ioDispatcher: CoroutineContext
) {

    suspend operator fun invoke() {
        withContext(ioDispatcher) {
            launch { userSessionRepository.removeUserId() }
            launch { userSessionRepository.removeAuthenticationTokens() }
            launch { signatureStorage.removeSignature() }
        }
    }
}