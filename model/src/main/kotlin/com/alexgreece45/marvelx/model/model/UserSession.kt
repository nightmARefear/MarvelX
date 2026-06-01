package com.alexgreece45.marvelx.model.model

/**
 * Contains all the user session data.
 *
 * @param userId The user ID.
 * @param authenticationTokens The authentication tokens data.
 */
data class UserSession(
    val userId: String,
    val authenticationTokens: AuthenticationTokens
)