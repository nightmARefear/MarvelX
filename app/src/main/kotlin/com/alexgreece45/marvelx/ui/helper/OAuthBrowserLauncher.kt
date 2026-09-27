package com.alexgreece45.marvelx.ui.helper

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.browser.auth.AuthTabIntent
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.net.toUri
import com.alexgreece45.marvelx.controllers.OAuthAuthorizationRequest
import com.alexgreece45.marvelx.controllers.OAuthRedirect

/**
 * Opens OAuth authorization requests in the browser and reports how they ended.
 *
 * Requests are opened in an ephemeral Auth Tab (no shared cookies, no history). Browsers without
 * Auth Tab support get a Custom Tab instead, automatically; in that case the redirect reaches the
 * app through `MainActivity`'s intent filters, and this launcher only learns that the tab closed.
 * If the Auth Tab rejects an `https` redirect (Digital Asset Links verification failed or timed
 * out), the request is retried once in a plain Custom Tab.
 *
 * Must be created during the activity's initialization (before it is `STARTED`), since it registers
 * an activity result launcher; the result survives the activity (and process) being recreated.
 *
 * @param onRedirectReceived    Called with the redirect URI delivered as the Auth Tab result.
 * @param onBrowserClosed       Called when the browser closed without delivering a redirect, with
 *                              the `state` of the request that was open (`null` if unknown, e.g.,
 *                              after process death).
 */
internal class OAuthBrowserLauncher(
    private val activity: ComponentActivity,
    private val onRedirectReceived: (Uri) -> Unit,
    private val onBrowserClosed: (state: String?) -> Unit,
) {
    private var openRequest: OAuthAuthorizationRequest? = null
    private var hasFallenBackToCustomTab = false

    private val resultLauncher: ActivityResultLauncher<Intent> =
        AuthTabIntent.registerActivityResultLauncher(activity, ::onAuthResult)

    fun launch(request: OAuthAuthorizationRequest) {
        val url = request.url.toUri()

        // The mock flavor's "authorization URL" is the redirect itself; deliver it straight to this
        // app (explicitly, so no other app can claim it).
        if (url.scheme != HTTPS_SCHEME) {
            try {
                activity.startActivity(Intent(Intent.ACTION_VIEW, url).setPackage(activity.packageName))
            } catch (_: ActivityNotFoundException) {
                // No intent filter of this app matches the scheme (misconfigured deep link scheme).
                onBrowserClosed(request.state)
            }
            return
        }

        openRequest = request
        hasFallenBackToCustomTab = false

        val authTabIntent = AuthTabIntent.Builder()
            .setEphemeralBrowsingEnabled(true)
            .build()

        try {
            when (val redirect = request.redirect) {
                is OAuthRedirect.CustomScheme ->
                    authTabIntent.launch(resultLauncher, url, redirect.scheme)

                is OAuthRedirect.Https ->
                    authTabIntent.launch(resultLauncher, url, redirect.host, redirect.path)
            }
        } catch (_: ActivityNotFoundException) {
            // No browser is installed at all.
            closeRequest()
        }
    }

    private fun onAuthResult(result: AuthTabIntent.AuthResult) {
        when (result.resultCode) {
            AuthTabIntent.RESULT_OK -> {
                val redirectUri = result.resultUri
                if (redirectUri == null) {
                    closeRequest()
                } else {
                    openRequest = null
                    onRedirectReceived(redirectUri)
                }
            }

            AuthTabIntent.RESULT_VERIFICATION_FAILED,
            AuthTabIntent.RESULT_VERIFICATION_TIMED_OUT -> {
                val request = openRequest
                if (request != null && !hasFallenBackToCustomTab) {
                    hasFallenBackToCustomTab = true
                    launchCustomTab(request)
                } else {
                    closeRequest()
                }
            }

            // Closed by the user, or a Custom Tab (fallback) was closed.
            else -> closeRequest()
        }
    }

    /**
     * Opens [request] in a plain (ephemeral, where supported) Custom Tab, through the same result
     * launcher so that closing it is still reported. The redirect arrives as a deep link.
     */
    private fun launchCustomTab(request: OAuthAuthorizationRequest) {
        val customTabIntent = CustomTabsIntent.Builder()
            .setEphemeralBrowsingEnabled(true)
            .build()
            .intent
            .setData(request.url.toUri())

        try {
            resultLauncher.launch(customTabIntent)
        } catch (_: ActivityNotFoundException) {
            closeRequest()
        }
    }

    private fun closeRequest() {
        val state = openRequest?.state
        openRequest = null
        onBrowserClosed(state)
    }

    private companion object {
        const val HTTPS_SCHEME = "https"
    }
}

/**
 * Provides the activity's [OAuthBrowserLauncher] to the screens that start an authorization
 * request. `null` in previews.
 */
internal val LocalOAuthBrowserLauncher = staticCompositionLocalOf<OAuthBrowserLauncher?> { null }
