package com.alexgreece45.marvelx.domain.models_api.userAuthData

/**
 * Data class that contains the required data for authentication purposes.
 *
 * @param accessToken       Auth access token. Does not include type (i.e., 'Bearer', etc.).
 * @param accessExpiresIn   UNIX timestamp that [accessToken] expires at.
 * @param refreshToken      Auth refresh token used for refreshing the [accessToken].
 * @param refreshExpiresIn  UNIX timestamp that [refreshToken] expires at.
 * @param tokenType         Type of [accessToken] (i.e. 'Bearer', etc.).
 * */
data class UserAuthData(
    val accessToken: String,
    val accessExpiresIn: Long,
    val refreshToken: String,
    val refreshExpiresIn: Long,
    val tokenType: String
)
