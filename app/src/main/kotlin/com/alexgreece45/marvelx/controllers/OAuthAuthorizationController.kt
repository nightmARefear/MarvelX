package com.alexgreece45.marvelx.controllers

import android.net.Uri
import android.util.Base64
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.time.Duration.Companion.minutes

/**
 * Owns the browser-based (Custom Tabs) leg of the OAuth 2.0 + PKCE authorization request: building
 * the `/authorize` URL with a fresh `code_verifier`/`code_challenge`/`state`, and resolving the
 * eventual redirect back into the app (caught as a deep link by `MainActivity`) into a result the
 * caller can exchange for tokens.
 *
 * The real (debug/release) and mock implementations only differ in how [buildAuthorizationUrl]
 * produces its URL — see the flavor-specific `OAuthAuthorizationControllerImpl` for each.
 *
 * All calls are expected on the main thread (ViewModel-triggered), matching how the rest of this
 * screen's state is handled — no cross-thread synchronization is needed here.
 */
interface OAuthAuthorizationController {

    /**
     * Builds the absolute browser URL for the authorization request tied to [tempAuthCode], and
     * remembers the `code_verifier`/`state` for this attempt so [awaitCallback] can validate the
     * redirect and hand back what's needed for the token exchange.
     */
    fun buildAuthorizationUrl(tempAuthCode: String): String

    /**
     * Suspends until the browser redirect calls back into the app via [onCallbackUri], or a timeout
     * elapses (the user abandoned the browser without completing the flow).
     */
    suspend fun awaitCallback(): OAuthCallbackResult

    /**
     * Called by `MainActivityViewModel` when the app receives the `/deeplink/oauth` redirect.
     * Resolves whoever is suspended in [awaitCallback].
     */
    fun onCallbackUri(uri: Uri)

    /**
     * Called by `MainActivityViewModel` when the app leaves the foreground. While an attempt is in
     * flight, this is the Custom Tab opening on top of the app.
     */
    fun onAppPaused()

    /**
     * Called by `MainActivityViewModel` when the app returns to the foreground, after the redirect
     * (if any) has had the chance to be delivered through [onCallbackUri]. If the app left the
     * foreground during the in-flight attempt and no callback arrived, the user abandoned the
     * browser (closed the tab, dismissed an app chooser, etc.), so whoever is suspended in
     * [awaitCallback] is resolved with [OAuthCallbackResult.Cancelled].
     */
    fun onAppResumed()
}

sealed interface OAuthCallbackResult {
    data class Success(val code: String, val codeVerifier: String) : OAuthCallbackResult
    data class ServerError(val errorCode: String) : OAuthCallbackResult
    data object StateMismatch : OAuthCallbackResult
    data object Timeout : OAuthCallbackResult
    data object Cancelled : OAuthCallbackResult
}

/**
 * Shared bookkeeping (state generation, callback validation, timeout) for every flavor's
 * [OAuthAuthorizationController]. Only [buildAuthorizationUrl] is left for subclasses to implement,
 * since it's the one part that legitimately differs between hitting the real backend and faking the
 * round trip for the `mock` build flavor.
 */
abstract class AbstractOAuthAuthorizationController(
    private val appLogger: AppLogger,
) : OAuthAuthorizationController {

    private val loggerTag = "OAuthAuthorizationController"

    protected var codeVerifier: String? = null
        private set
    protected var state: String? = null
        private set
    private var pendingCallback: CompletableDeferred<OAuthCallbackResult>? = null

    /**
     * Whether the app left the foreground while the current attempt was in flight, i.e., the
     * browser was actually shown. Guards [onAppResumed] from cancelling an attempt whose browser
     * hasn't been opened yet.
     */
    private var leftAppDuringAttempt: Boolean = false

    /**
     * Starts a new in-flight attempt: generates and stores a fresh `code_verifier`/`state`, and
     * returns them for [buildAuthorizationUrl] to use when constructing its URL.
     */
    protected fun startNewAttempt(): Pair<String, String> {
        val verifier = createRandomUrlSafeToken()
        val newState = createRandomUrlSafeToken()
        codeVerifier = verifier
        state = newState
        pendingCallback = CompletableDeferred()
        leftAppDuringAttempt = false
        return verifier to newState
    }

    override suspend fun awaitCallback(): OAuthCallbackResult {
        val deferred = pendingCallback ?: return OAuthCallbackResult.Timeout
        return withTimeoutOrNull(CALLBACK_TIMEOUT_MINUTES.minutes) { deferred.await() }
            ?: OAuthCallbackResult.Timeout
    }

    override fun onCallbackUri(uri: Uri) {
        val expectedState = state
        val verifier = codeVerifier
        val deferred = pendingCallback

        if (expectedState == null || verifier == null || deferred == null) {
            appLogger.e(loggerTag) { "(onCallbackUri) Received an OAuth callback with no attempt in flight." }
            return
        }

        if (uri.getQueryParameter("state") != expectedState) {
            appLogger.e(loggerTag) { "(onCallbackUri) State mismatch on OAuth callback." }
            deferred.complete(OAuthCallbackResult.StateMismatch)
            clear()
            return
        }

        val error = uri.getQueryParameter("error")
        if (error != null) {
            appLogger.e(loggerTag) { "(onCallbackUri) OAuth callback returned an error: $error" }
            deferred.complete(OAuthCallbackResult.ServerError(error))
            clear()
            return
        }

        val code = uri.getQueryParameter("code")
        if (code.isNullOrBlank()) {
            appLogger.e(loggerTag) { "(onCallbackUri) OAuth callback missing both 'code' and 'error'." }
            deferred.complete(OAuthCallbackResult.ServerError("missing_code"))
            clear()
            return
        }

        deferred.complete(OAuthCallbackResult.Success(code = code, codeVerifier = verifier))
        clear()
    }

    override fun onAppPaused() {
        if (pendingCallback != null) {
            leftAppDuringAttempt = true
        }
    }

    override fun onAppResumed() {
        val deferred = pendingCallback ?: return
        if (!leftAppDuringAttempt) return

        appLogger.e(loggerTag) { "(onAppResumed) Returned to the app without an OAuth callback; the attempt was abandoned." }
        deferred.complete(OAuthCallbackResult.Cancelled)
        clear()
    }

    private fun clear() {
        codeVerifier = null
        state = null
        pendingCallback = null
        leftAppDuringAttempt = false
    }

    protected fun createRandomUrlSafeToken(): String {
        val bytes = ByteArray(64)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    protected fun createCodeChallenge(verifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.UTF_8))
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    private companion object {
        const val CALLBACK_TIMEOUT_MINUTES = 5L
    }
}
