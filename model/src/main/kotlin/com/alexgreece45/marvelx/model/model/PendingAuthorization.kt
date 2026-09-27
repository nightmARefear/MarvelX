package com.alexgreece45.marvelx.model.model

/**
 * An OAuth 2.0 + PKCE authorization request that was handed to the browser, and whose redirect
 * hasn't been processed yet. It is persisted so that the login can still be completed if the OS
 * kills the app process while the browser is in the foreground.
 *
 * @param state                     The `state` sent with the authorization request.
 * @param codeVerifier              The PKCE `code_verifier` whose challenge was sent with the
 *                                  authorization request.
 * @param redirectUri               The exact `redirect_uri` sent with the authorization request,
 *                                  which the token exchange must repeat.
 * @param userId                    The ID of the user the authorization request was made for.
 * @param shouldRememberUserSession Whether the user session should be remembered once the login
 *                                  completes.
 * @param createdAtEpochSeconds     UNIX timestamp of when the authorization request was created.
 */
data class PendingAuthorization(
    val state: String,
    val codeVerifier: String,
    val redirectUri: String,
    val userId: String,
    val shouldRememberUserSession: Boolean,
    val createdAtEpochSeconds: Long
)
