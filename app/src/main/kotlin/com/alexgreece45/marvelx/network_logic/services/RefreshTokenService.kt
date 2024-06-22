package com.alexgreece45.marvelx.network_logic.services

import com.alexgreece45.marvelx.network_logic.models_api.token.ResponseToken
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * The token service for the refresh access token call only.
 */
interface RefreshTokenService {
    @POST("/api/private/marvelx/user/token")
    @FormUrlEncoded
    suspend fun refreshAccessToken(
        @Field("client_id") clientId: String,
        @Field("refresh_token") refreshToken: String,
        @Field("grant_type") grantType: String = "refresh_token"
    ): Response<ResponseToken>
}
