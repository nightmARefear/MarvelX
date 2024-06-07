package com.alexgreece45.marvelx.ui.screens.login

import android.content.Intent
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.ui.screens.login.model.GoogleUserDomain
import com.alexgreece45.marvelx.ui.screens.login.model.LoginWithGoogleResult
import com.alexgreece45.marvelx.ui.screens.login.model.LoginWithIntentWithGoogleResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

/**
 * Google firebase authentication UI client. This class enables to login a user through the
 * "login with google" button.
 */
class GoogleAuthUiClient(private val signInClient: SignInClient) {
    private val auth = Firebase.auth

    /**
     * Attempts to login the user.
     */
    suspend fun login(): LoginWithGoogleResult {
        return try {
            val result = signInClient.beginSignIn(buildSignInRequest()).await()
            LoginWithGoogleResult.Success(result.pendingIntent.intentSender)
        } catch (e: Exception) {
            // TODO: Log error
            if (e is CancellationException) throw e
            LoginWithGoogleResult.Failed(e.toString())
        }
    }

    suspend fun loginWithIntent(intent: Intent): LoginWithIntentWithGoogleResult {
        val credential = signInClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredentials = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredentials).await().user
            LoginWithIntentWithGoogleResult.Success(
                googleUserDomain = user?.run {
                    GoogleUserDomain(
                        userId = uid,
                        displayName = displayName,
                        email = email
                    )
                }
            )
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            LoginWithIntentWithGoogleResult.Failed(e.message.toString())
        }
    }

    /**
     * Logouts the user.
     */
    suspend fun logout() {
        try {
            signInClient.signOut().await()
            auth.signOut()
        } catch (e: Exception) {
            // TODO: Log error
            if (e is CancellationException) throw e
        }
    }

    fun getLoggedInUser(): GoogleUserDomain? = auth.currentUser?.run {
        GoogleUserDomain(
            userId = uid,
            displayName = displayName,
            email = email
        )
    }

    /**
     * Builds a sign in request and returns it.
     */
    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.FIREBASE_SERVER_CLIENT_ID)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}