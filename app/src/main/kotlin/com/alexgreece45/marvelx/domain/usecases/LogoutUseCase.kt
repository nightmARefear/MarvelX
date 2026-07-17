package com.alexgreece45.marvelx.domain.usecases

import com.alexgreece45.marvelx.controllers.RefreshTokenController
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.data.repositories.UserSessionRepository
import com.alexgreece45.marvelx.data.repository.AutoLogoutRepository
import com.alexgreece45.marvelx.data.repository.LoginRepository
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.core.data.helper.RepositoryResult
import org.koin.core.annotation.Factory

/**
 * Performs the necessary logout operations such as cleaning the local data, closing the
 * websocket connection, and more.
 */
@Factory
class LogoutUseCase(
    private val loginRepository: LoginRepository,
    private val configOauthLogic: ConfigOauthLogic,
    private val refreshTokenController: RefreshTokenController,
    private val websocketController: WebsocketController,
    private val userRepository: UserRepository,
    private val autoLogoutRepository: AutoLogoutRepository,
    private val removeUserCredentialsUseCase: RemoveUserCredentialsUseCase,
    private val userSessionRepository: UserSessionRepository
) {

    suspend operator fun invoke(): RepositoryResult<String> {
        val refreshToken = userSessionRepository.getRefreshToken()

        websocketController.close()
        userRepository.setUserLoggedIn(false)
        removeUserCredentialsUseCase()
        autoLogoutRepository.removeAutoLogoutTimestamp()
        refreshTokenController.stopWorker()

        if (refreshToken.isNullOrEmpty()) {
            return RepositoryResult.Success("")
        }

        val logoutResult = loginRepository.logout(configOauthLogic.clientId, refreshToken)

        return logoutResult
    }
}