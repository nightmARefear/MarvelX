package com.alexgreece45.marvelx.domain.usecases

import com.alexgreece45.marvelx.data.repository.AutoLogoutRepository
import com.alexgreece45.marvelx.domain.interactors.GrandNewAccessTokenPartialState
import com.alexgreece45.marvelx.domain.interactors.LoginInteractor
import com.alexgreece45.marvelx.domain.interactors.PostLoginPartialState
import com.alexgreece45.marvelx.domain.interactors.PostLoginSession
import kotlinx.coroutines.flow.first
import org.koin.core.annotation.Factory

/**
 * Completes an OAuth 2.0 + PKCE login once the authorization redirect delivered a `code`:
 * exchanges it (with its `code_verifier` and the authorization request's exact `redirect_uri`) for
 * tokens, performs the post-login (which stores the session), and stores whether the session
 * should be remembered.
 *
 * Shared by the screens that start the authorization request (OTP, signup) and by the cold start
 * that resumes a request whose process was killed while the browser was open.
 */
@Factory
class CompleteOAuthLoginUseCase(
    private val loginInteractor: LoginInteractor,
    private val autoLogoutRepository: AutoLogoutRepository,
) {

    suspend operator fun invoke(
        code: String,
        codeVerifier: String,
        redirectUri: String,
        userId: String,
        shouldRememberUserSession: Boolean
    ): CompleteOAuthLoginResult {
        val tokenState = loginInteractor.grandNewAccessToken(
            authCode = code,
            codeVerifier = codeVerifier,
            redirectUri = redirectUri
        ).first()

        val authenticationTokens = when (tokenState) {
            is GrandNewAccessTokenPartialState.Failed ->
                return CompleteOAuthLoginResult.TokenExchangeFailed(tokenState.errorMessage)

            is GrandNewAccessTokenPartialState.Success -> tokenState.authenticationTokens
        }

        val postLoginState = loginInteractor.postLogin(
            PostLoginSession.New(
                userId = userId,
                authenticationTokens = authenticationTokens
            )
        ).first()

        return when (postLoginState) {
            is PostLoginPartialState.Failed ->
                CompleteOAuthLoginResult.PostLoginFailed(postLoginState.errorMessage)

            PostLoginPartialState.Success -> {
                autoLogoutRepository.setRememberUserSession(shouldRememberUserSession)
                CompleteOAuthLoginResult.Success
            }
        }
    }
}

sealed interface CompleteOAuthLoginResult {
    data object Success : CompleteOAuthLoginResult
    data class TokenExchangeFailed(val errorMessage: String) : CompleteOAuthLoginResult
    data class PostLoginFailed(val errorMessage: String) : CompleteOAuthLoginResult
}
