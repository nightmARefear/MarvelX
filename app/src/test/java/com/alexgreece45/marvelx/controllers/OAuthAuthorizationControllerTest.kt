package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.repository.PendingAuthorizationRepository
import com.alexgreece45.marvelx.model.model.PendingAuthorization
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OAuthAuthorizationControllerTest {

    private val loginContext = OAuthLoginContext(userId = "user-1", shouldRememberUserSession = true)

    @Test
    fun `GIVEN an attempt in flight WHEN the redirect carries its state and a code THEN resolves Success and discards the persisted attempt`() = runBlocking {
        val repository = FakePendingAuthorizationRepository()
        val controller = TestController(repository)
        val request = controller.startAuthorization("temp", loginContext)
        val verifier = repository.pending!!.codeVerifier
        val result = async(Dispatchers.Unconfined, CoroutineStart.UNDISPATCHED) { controller.awaitCallback() }

        controller.onCallback(params(state = request.state, code = "the-code"))

        assertEquals(
            OAuthCallbackResult.Success(code = "the-code", codeVerifier = verifier, redirectUri = REDIRECT_URI),
            result.await()
        )
        assertNull(repository.pending)
    }

    @Test
    fun `WHEN an attempt starts THEN persists its state, verifier and login context before returning`() = runBlocking {
        val repository = FakePendingAuthorizationRepository()
        val controller = TestController(repository, now = 1_000L)

        val request = controller.startAuthorization("temp", loginContext)

        val pending = repository.pending!!
        assertEquals(request.state, pending.state)
        assertEquals(REDIRECT_URI, pending.redirectUri)
        assertEquals("user-1", pending.userId)
        assertEquals(true, pending.shouldRememberUserSession)
        assertEquals(1_000L, pending.createdAtEpochSeconds)
        assertEquals(Pkce.createCodeChallenge(pending.codeVerifier), controller.lastCodeChallenge)
    }

    @Test
    fun `GIVEN an attempt in flight WHEN the redirect carries another state THEN resolves StateMismatch`() = runBlocking {
        val controller = TestController(FakePendingAuthorizationRepository())
        controller.startAuthorization("temp", loginContext)
        val result = async(Dispatchers.Unconfined, CoroutineStart.UNDISPATCHED) { controller.awaitCallback() }

        controller.onCallback(params(state = "forged", code = "the-code"))

        assertEquals(OAuthCallbackResult.StateMismatch, result.await())
    }

    @Test
    fun `GIVEN an attempt in flight WHEN the redirect carries an error THEN resolves ServerError`() = runBlocking {
        val controller = TestController(FakePendingAuthorizationRepository())
        val request = controller.startAuthorization("temp", loginContext)
        val result = async(Dispatchers.Unconfined, CoroutineStart.UNDISPATCHED) { controller.awaitCallback() }

        controller.onCallback(params(state = request.state, error = "access_denied", errorDescription = "expired"))

        assertEquals(OAuthCallbackResult.ServerError("access_denied"), result.await())
    }

    @Test
    fun `GIVEN an attempt in flight WHEN the redirect has neither code nor error THEN resolves ServerError missing_code`() = runBlocking {
        val controller = TestController(FakePendingAuthorizationRepository())
        val request = controller.startAuthorization("temp", loginContext)
        val result = async(Dispatchers.Unconfined, CoroutineStart.UNDISPATCHED) { controller.awaitCallback() }

        controller.onCallback(params(state = request.state))

        assertEquals(OAuthCallbackResult.ServerError("missing_code"), result.await())
    }

    @Test
    fun `GIVEN a resolved attempt WHEN the same redirect is delivered again THEN it is ignored`() = runBlocking {
        val repository = FakePendingAuthorizationRepository()
        val controller = TestController(repository)
        val request = controller.startAuthorization("temp", loginContext)
        controller.onCallback(params(state = request.state, code = "the-code"))

        controller.onCallback(params(state = request.state, code = "the-code"))

        assertNull(controller.takeRestoredAuthorization())
        assertEquals(0, repository.getCount)
    }

    @Test
    fun `GIVEN an attempt in flight WHEN its browser is closed THEN resolves Cancelled`() = runBlocking {
        val repository = FakePendingAuthorizationRepository()
        val controller = TestController(repository)
        val request = controller.startAuthorization("temp", loginContext)
        val result = async(Dispatchers.Unconfined, CoroutineStart.UNDISPATCHED) { controller.awaitCallback() }

        controller.onBrowserClosed(request.state)

        assertEquals(OAuthCallbackResult.Cancelled, result.await())
        assertNull(repository.pending)
    }

    @Test
    fun `GIVEN an attempt in flight WHEN the browser of another attempt is closed THEN the attempt stays in flight`() = runBlocking {
        val controller = TestController(FakePendingAuthorizationRepository())
        val request = controller.startAuthorization("temp", loginContext)

        controller.onBrowserClosed("another-state")
        controller.onBrowserClosed(null)
        val result = async(Dispatchers.Unconfined, CoroutineStart.UNDISPATCHED) { controller.awaitCallback() }
        controller.onCallback(params(state = request.state, code = "the-code"))

        assertEquals(OAuthCallbackResult.Success::class, result.await()::class)
    }

    @Test
    fun `GIVEN an attempt in flight WHEN a new attempt starts THEN the previous one resolves Cancelled`() = runBlocking {
        val repository = FakePendingAuthorizationRepository()
        val controller = TestController(repository)
        controller.startAuthorization("temp-1", loginContext)
        val firstResult = async(Dispatchers.Unconfined, CoroutineStart.UNDISPATCHED) { controller.awaitCallback() }

        val secondRequest = controller.startAuthorization("temp-2", loginContext)

        assertEquals(OAuthCallbackResult.Cancelled, firstResult.await())
        assertEquals(secondRequest.state, repository.pending!!.state)
    }

    @Test
    fun `GIVEN an attempt persisted by a killed process WHEN its redirect arrives in a new process THEN it can be resumed once`() = runBlocking {
        val repository = FakePendingAuthorizationRepository()
        val request = TestController(repository, now = 1_000L).startAuthorization("temp", loginContext)
        val verifier = repository.pending!!.codeVerifier
        val newProcessController = TestController(repository, now = 1_060L)

        newProcessController.onCallback(params(state = request.state, code = "the-code"))

        assertEquals(true, newProcessController.hasRestoredAuthorization())
        assertEquals(
            RestoredOAuthAuthorization.Success(
                code = "the-code",
                codeVerifier = verifier,
                redirectUri = REDIRECT_URI,
                loginContext = loginContext
            ),
            newProcessController.takeRestoredAuthorization()
        )
        assertNull(newProcessController.takeRestoredAuthorization())
        assertEquals(false, newProcessController.hasRestoredAuthorization())
        assertNull(repository.pending)
    }

    @Test
    fun `GIVEN an attempt persisted by a killed process WHEN a redirect with another state arrives THEN the restore fails`() = runBlocking {
        val repository = FakePendingAuthorizationRepository()
        TestController(repository).startAuthorization("temp", loginContext)
        val newProcessController = TestController(repository)

        newProcessController.onCallback(params(state = "forged", code = "the-code"))

        assertEquals(RestoredOAuthAuthorization.Failed, newProcessController.takeRestoredAuthorization())
        assertNull(repository.pending)
    }

    @Test
    fun `GIVEN an expired attempt persisted by a killed process WHEN its redirect arrives THEN the restore fails`() = runBlocking {
        val repository = FakePendingAuthorizationRepository()
        val request = TestController(repository, now = 1_000L).startAuthorization("temp", loginContext)
        val newProcessController = TestController(repository, now = 1_000L + 11 * 60)

        newProcessController.onCallback(params(state = request.state, code = "the-code"))

        assertEquals(RestoredOAuthAuthorization.Failed, newProcessController.takeRestoredAuthorization())
    }

    @Test
    fun `GIVEN an attempt persisted by a killed process WHEN no redirect arrives THEN nothing is resumed and it is discarded`() = runBlocking {
        val repository = FakePendingAuthorizationRepository()
        TestController(repository).startAuthorization("temp", loginContext)
        val newProcessController = TestController(repository)

        val restored = newProcessController.takeRestoredAuthorization()

        assertNull(restored)
        assertNull(repository.pending)
    }

    @Test
    fun `GIVEN nothing persisted WHEN a stray redirect arrives in a new process THEN nothing is resumed`() = runBlocking {
        val newProcessController = TestController(FakePendingAuthorizationRepository())

        newProcessController.onCallback(params(state = "any", code = "the-code"))

        assertEquals(false, newProcessController.hasRestoredAuthorization())
        assertNull(newProcessController.takeRestoredAuthorization())
    }

    @Test
    fun `GIVEN the https redirect is enabled WHEN an attempt starts THEN the request redirects to the https host and path`() = runBlocking {
        val controller = TestController(
            FakePendingAuthorizationRepository(),
            configOauthLogic = FakeConfigOauthLogic(isHttpsRedirectEnabled = true)
        )

        val request = controller.startAuthorization("temp", loginContext)

        assertEquals(OAuthRedirect.Https(host = HOST, path = PATH), request.redirect)
    }

    private fun params(
        state: String?,
        code: String? = null,
        error: String? = null,
        errorDescription: String? = null
    ) = OAuthCallbackParams(state = state, code = code, error = error, errorDescription = errorDescription)

    private class TestController(
        repository: PendingAuthorizationRepository,
        private val now: Long = 1_000L,
        configOauthLogic: ConfigOauthLogic = FakeConfigOauthLogic(isHttpsRedirectEnabled = false),
    ) : AbstractOAuthAuthorizationController(NoOpAppLogger, repository, configOauthLogic) {

        var lastCodeChallenge: String? = null

        override suspend fun startAuthorization(
            tempAuthCode: String,
            loginContext: OAuthLoginContext
        ): OAuthAuthorizationRequest {
            val attempt = startNewAttempt(loginContext)
            lastCodeChallenge = attempt.codeChallenge
            return OAuthAuthorizationRequest(
                url = "https://$HOST/authorize?state=${attempt.state}",
                redirect = redirect,
                state = attempt.state
            )
        }

        override fun currentEpochSeconds(): Long = now
    }

    private class FakePendingAuthorizationRepository : PendingAuthorizationRepository {
        var pending: PendingAuthorization? = null
        var getCount = 0

        override suspend fun getPendingAuthorization(): PendingAuthorization? {
            getCount++
            return pending
        }

        override suspend fun setPendingAuthorization(pendingAuthorization: PendingAuthorization) {
            pending = pendingAuthorization
        }

        override suspend fun removePendingAuthorization() {
            pending = null
        }
    }

    private class FakeConfigOauthLogic(override val isHttpsRedirectEnabled: Boolean) : ConfigOauthLogic {
        override val tokenPath = "/api/private/marvelx/user/token"
        override val clientId = "mobile"
        override val codeChallengeMethod = "S256"
        override val authorizationPath = "/api/private/marvelx/user/authorize"
        override val redirectScheme = if (isHttpsRedirectEnabled) "https" else "marvelx"
        override val redirectHost = HOST
        override val redirectPath = PATH
        override val redirectUri = if (isHttpsRedirectEnabled) "https://$HOST$PATH" else REDIRECT_URI
    }

    private object NoOpAppLogger : AppLogger {
        override fun installLogger() = Unit
        override fun d(message: () -> String) = Unit
        override fun d(tag: String?, message: () -> String) = Unit
        override fun i(message: () -> String) = Unit
        override fun i(tag: String?, message: () -> String) = Unit
        override fun e(message: () -> String) = Unit
        override fun e(tag: String?, message: () -> String) = Unit
        override fun e(tag: String?, throwable: Throwable) = Unit
    }

    private companion object {
        const val HOST = "backend.example.com"
        const val PATH = "/deeplink/oauth"
        const val REDIRECT_URI = "marvelx://$HOST$PATH"
    }
}
