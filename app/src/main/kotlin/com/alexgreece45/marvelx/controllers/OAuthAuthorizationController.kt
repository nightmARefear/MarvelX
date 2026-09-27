package com.alexgreece45.marvelx.controllers

import android.net.Uri
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.data.repository.PendingAuthorizationRepository
import com.alexgreece45.marvelx.model.model.PendingAuthorization
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.minutes

/**
 * Owns the browser-based leg of the OAuth 2.0 + PKCE authorization request: generating a fresh
 * `code_verifier`/`code_challenge`/`state` per attempt, persisting the attempt so it survives
 * process death, and resolving the eventual redirect back into the app into a result the caller
 * can exchange for tokens.
 *
 * The redirect can reach the app through two paths — the Auth Tab's activity result, or the
 * `MainActivity` intent filter (Custom Tab fallback, or any browser without Auth Tab support) —
 * so it is handled idempotently: the first valid delivery resolves the attempt, later ones are
 * ignored.
 *
 * The real (debug/release) and mock implementations only differ in how [startAuthorization]
 * produces its URL — see the flavor-specific `OAuthAuthorizationControllerImpl` for each.
 *
 * All calls are expected on the main thread (ViewModel-triggered), so no cross-thread
 * synchronization is needed here.
 */
interface OAuthAuthorizationController {

    /**
     * Starts a new authorization attempt for [tempAuthCode]: generates and persists a fresh
     * `code_verifier`/`state` together with [loginContext] (so the login can be resumed if the
     * process dies while the browser is open), and returns what the browser has to open. Any
     * previous attempt is resolved as [OAuthCallbackResult.Cancelled].
     */
    suspend fun startAuthorization(
        tempAuthCode: String,
        loginContext: OAuthLoginContext
    ): OAuthAuthorizationRequest

    /**
     * Suspends until the current attempt is resolved (redirect received, browser closed), or a
     * timeout elapses.
     */
    suspend fun awaitCallback(): OAuthCallbackResult

    /**
     * Called by `MainActivityViewModel` with the redirect URI, received either as the Auth Tab
     * result or as a deep link.
     */
    suspend fun onCallbackUri(uri: Uri)

    /**
     * Called by `MainActivityViewModel` when the browser that was opened for the attempt with
     * [state] was closed without delivering a redirect (after a grace period, since on the
     * Custom Tab fallback path the redirect intent and the close can arrive in either order).
     */
    suspend fun onBrowserClosed(state: String?)

    /**
     * Returns, once, the outcome of an attempt that outlived the app process that started it —
     * i.e., the redirect arrived after the OS had killed the process while the browser was open.
     * Returns `null` if there is nothing to resume, in which case any attempt persisted by a
     * previous process is discarded as abandoned.
     */
    suspend fun takeRestoredAuthorization(): RestoredOAuthAuthorization?

    /**
     * Whether a redirect for an attempt of a killed process was received, and its outcome hasn't
     * been taken through [takeRestoredAuthorization] yet.
     */
    fun hasRestoredAuthorization(): Boolean
}

/**
 * Who the authorization request is for, persisted with it so the login can be completed from a
 * fresh process.
 */
data class OAuthLoginContext(
    val userId: String,
    val shouldRememberUserSession: Boolean
)

/**
 * What the browser has to open for an authorization attempt.
 *
 * @param url       The absolute URL to open.
 * @param redirect  Where the authorization server redirects back to.
 * @param state     The attempt's `state`, used to tie a browser close back to this attempt.
 */
data class OAuthAuthorizationRequest(
    val url: String,
    val redirect: OAuthRedirect,
    val state: String
)

sealed interface OAuthRedirect {
    data class CustomScheme(val scheme: String) : OAuthRedirect
    data class Https(val host: String, val path: String) : OAuthRedirect
}

sealed interface OAuthCallbackResult {
    data class Success(
        val code: String,
        val codeVerifier: String,
        val redirectUri: String
    ) : OAuthCallbackResult

    data class ServerError(val errorCode: String) : OAuthCallbackResult
    data object StateMismatch : OAuthCallbackResult
    data object Timeout : OAuthCallbackResult
    data object Cancelled : OAuthCallbackResult
}

sealed interface RestoredOAuthAuthorization {
    data class Success(
        val code: String,
        val codeVerifier: String,
        val redirectUri: String,
        val loginContext: OAuthLoginContext
    ) : RestoredOAuthAuthorization

    /**
     * A redirect arrived for a persisted attempt, but it can't be completed (expired, `state`
     * mismatch, or an `error` response).
     */
    data object Failed : RestoredOAuthAuthorization
}

/**
 * The query parameters of an authorization redirect.
 */
internal data class OAuthCallbackParams(
    val state: String?,
    val code: String?,
    val error: String?,
    val errorDescription: String?
) {
    companion object {
        fun from(uri: Uri): OAuthCallbackParams = OAuthCallbackParams(
            state = uri.getQueryParameter("state"),
            code = uri.getQueryParameter("code"),
            error = uri.getQueryParameter("error"),
            errorDescription = uri.getQueryParameter("error_description")
        )
    }
}

/**
 * Shared bookkeeping (PKCE/state generation, persistence, callback validation, timeout) for every
 * flavor's [OAuthAuthorizationController]. Only [startAuthorization] is left for subclasses to
 * implement, since it's the one part that legitimately differs between hitting the real backend
 * and faking the round trip for the `mock` build flavor.
 */
abstract class AbstractOAuthAuthorizationController(
    private val appLogger: AppLogger,
    private val pendingAuthorizationRepository: PendingAuthorizationRepository,
    private val configOauthLogic: ConfigOauthLogic,
) : OAuthAuthorizationController {

    private val loggerTag = "OAuthAuthorizationController"

    private class InFlightAttempt(
        val state: String,
        val codeVerifier: String,
        val redirectUri: String,
        val result: CompletableDeferred<OAuthCallbackResult> = CompletableDeferred()
    )

    private var attempt: InFlightAttempt? = null

    /**
     * Whether this process has started an attempt. A redirect that finds no in-flight attempt is
     * only treated as a candidate for resuming a persisted attempt when this is `false`; otherwise
     * it is a duplicate or stray delivery for an attempt this process already resolved.
     */
    private var hasStartedAttemptInProcess = false

    /**
     * The outcome of resuming a persisted attempt, set when a redirect arrives in a process that
     * didn't start the attempt. Consumed once by [takeRestoredAuthorization].
     */
    private var restoredAuthorization: CompletableDeferred<RestoredOAuthAuthorization?>? = null
    private var restoredAuthorizationOutcome: RestoredOAuthAuthorization? = null
    private var isRestoredAuthorizationTaken = false

    /**
     * The [OAuthRedirect] matching [ConfigOauthLogic.redirectUri].
     */
    protected val redirect: OAuthRedirect
        get() = if (configOauthLogic.isHttpsRedirectEnabled) {
            OAuthRedirect.Https(
                host = configOauthLogic.redirectHost,
                path = configOauthLogic.redirectPath
            )
        } else {
            OAuthRedirect.CustomScheme(scheme = configOauthLogic.redirectScheme)
        }

    /**
     * Starts a new in-flight attempt: resolves the previous one (if any) as cancelled, generates a
     * fresh `code_verifier`/`state`, and persists them with [loginContext] before returning them.
     */
    protected suspend fun startNewAttempt(loginContext: OAuthLoginContext): PkceAttempt {
        attempt?.result?.complete(OAuthCallbackResult.Cancelled)

        val newAttempt = InFlightAttempt(
            state = Pkce.createRandomUrlSafeToken(),
            codeVerifier = Pkce.createRandomUrlSafeToken(),
            redirectUri = configOauthLogic.redirectUri
        )
        attempt = newAttempt
        hasStartedAttemptInProcess = true

        pendingAuthorizationRepository.setPendingAuthorization(
            PendingAuthorization(
                state = newAttempt.state,
                codeVerifier = newAttempt.codeVerifier,
                redirectUri = newAttempt.redirectUri,
                userId = loginContext.userId,
                shouldRememberUserSession = loginContext.shouldRememberUserSession,
                createdAtEpochSeconds = currentEpochSeconds()
            )
        )

        return PkceAttempt(
            state = newAttempt.state,
            codeChallenge = Pkce.createCodeChallenge(newAttempt.codeVerifier),
            redirectUri = newAttempt.redirectUri
        )
    }

    override suspend fun awaitCallback(): OAuthCallbackResult {
        val current = attempt ?: return OAuthCallbackResult.Timeout
        val result = withTimeoutOrNull(CALLBACK_TIMEOUT_MINUTES.minutes) { current.result.await() }
        if (result != null) return result

        appLogger.e(loggerTag) { "(awaitCallback) Timed out waiting for the OAuth callback." }
        finish(current, OAuthCallbackResult.Timeout)
        return OAuthCallbackResult.Timeout
    }

    override suspend fun onCallbackUri(uri: Uri) {
        onCallback(OAuthCallbackParams.from(uri))
    }

    internal suspend fun onCallback(params: OAuthCallbackParams) {
        val current = attempt
        if (current == null) {
            onCallbackWithoutAttempt(params)
            return
        }

        if (params.state != current.state) {
            appLogger.e(loggerTag) { "(onCallback) State mismatch on OAuth callback." }
            finish(current, OAuthCallbackResult.StateMismatch)
            return
        }

        if (params.error != null) {
            appLogger.e(loggerTag) { "(onCallback) OAuth callback returned an error: ${params.describeError()}" }
            finish(current, OAuthCallbackResult.ServerError(params.error))
            return
        }

        if (params.code.isNullOrBlank()) {
            appLogger.e(loggerTag) { "(onCallback) OAuth callback missing both 'code' and 'error'." }
            finish(current, OAuthCallbackResult.ServerError(MISSING_CODE_ERROR))
            return
        }

        finish(
            current,
            OAuthCallbackResult.Success(
                code = params.code,
                codeVerifier = current.codeVerifier,
                redirectUri = current.redirectUri
            )
        )
    }

    override suspend fun onBrowserClosed(state: String?) {
        val current = attempt ?: return
        if (state == null || state != current.state) return

        appLogger.e(loggerTag) { "(onBrowserClosed) The browser was closed without an OAuth callback; the attempt was abandoned." }
        finish(current, OAuthCallbackResult.Cancelled)
    }

    override suspend fun takeRestoredAuthorization(): RestoredOAuthAuthorization? {
        val restored = restoredAuthorization
        if (restored == null) {
            // No redirect arrived in this process, so an attempt persisted by a previous process
            // was abandoned (e.g., the browser was closed after the process had been killed).
            if (!hasStartedAttemptInProcess) {
                pendingAuthorizationRepository.removePendingAuthorization()
            }
            return null
        }

        if (isRestoredAuthorizationTaken) return null
        isRestoredAuthorizationTaken = true
        return restored.await()
    }

    override fun hasRestoredAuthorization(): Boolean =
        restoredAuthorizationOutcome != null && !isRestoredAuthorizationTaken

    /**
     * Handles a redirect that found no in-flight attempt: either a duplicate/stray delivery, or
     * the redirect of an attempt started by a process that has since been killed.
     */
    private suspend fun onCallbackWithoutAttempt(params: OAuthCallbackParams) {
        if (hasStartedAttemptInProcess || restoredAuthorization != null) {
            appLogger.e(loggerTag) { "(onCallback) Received an OAuth callback with no attempt in flight." }
            return
        }

        val restored = CompletableDeferred<RestoredOAuthAuthorization?>()
        restoredAuthorization = restored

        val pending = pendingAuthorizationRepository.getPendingAuthorization()
        if (pending == null) {
            appLogger.e(loggerTag) { "(onCallback) Received an OAuth callback with no attempt in flight or persisted." }
            restored.complete(null)
            return
        }
        pendingAuthorizationRepository.removePendingAuthorization()

        val outcome = when {
            currentEpochSeconds() - pending.createdAtEpochSeconds > PENDING_AUTHORIZATION_MAX_AGE_SECONDS -> {
                appLogger.e(loggerTag) { "(onCallback) The persisted OAuth attempt has expired." }
                RestoredOAuthAuthorization.Failed
            }

            params.state != pending.state -> {
                appLogger.e(loggerTag) { "(onCallback) State mismatch on the persisted OAuth attempt." }
                RestoredOAuthAuthorization.Failed
            }

            params.error != null -> {
                appLogger.e(loggerTag) { "(onCallback) OAuth callback returned an error: ${params.describeError()}" }
                RestoredOAuthAuthorization.Failed
            }

            params.code.isNullOrBlank() -> {
                appLogger.e(loggerTag) { "(onCallback) OAuth callback missing both 'code' and 'error'." }
                RestoredOAuthAuthorization.Failed
            }

            else -> {
                appLogger.d(loggerTag) { "(onCallback) Resuming the OAuth attempt of a previous process." }
                RestoredOAuthAuthorization.Success(
                    code = params.code,
                    codeVerifier = pending.codeVerifier,
                    redirectUri = pending.redirectUri,
                    loginContext = OAuthLoginContext(
                        userId = pending.userId,
                        shouldRememberUserSession = pending.shouldRememberUserSession
                    )
                )
            }
        }

        restoredAuthorizationOutcome = outcome
        restored.complete(outcome)
    }

    /**
     * Resolves [target] with [result] and discards it, unless a newer attempt has replaced it in
     * the meantime (in which case the newer attempt and its persisted copy are left untouched).
     */
    private suspend fun finish(target: InFlightAttempt, result: OAuthCallbackResult) {
        target.result.complete(result)
        if (attempt !== target) return

        attempt = null
        pendingAuthorizationRepository.removePendingAuthorization()
    }

    private fun OAuthCallbackParams.describeError(): String =
        errorDescription?.let { "$error ($it)" } ?: error.orEmpty()

    /**
     * The current time, in UNIX epoch seconds. Overridable for tests.
     */
    protected open fun currentEpochSeconds(): Long = System.currentTimeMillis() / 1000

    /**
     * What a subclass needs to build its authorization URL.
     */
    protected data class PkceAttempt(
        val state: String,
        val codeChallenge: String,
        val redirectUri: String
    )

    private companion object {
        const val CALLBACK_TIMEOUT_MINUTES = 5L
        const val MISSING_CODE_ERROR = "missing_code"

        /**
         * How long a persisted attempt can still be resumed from a fresh process. The backend
         * only accepts the `code` for 2 minutes, so anything older can't be exchanged anyway.
         */
        const val PENDING_AUTHORIZATION_MAX_AGE_SECONDS = 10 * 60L
    }
}
