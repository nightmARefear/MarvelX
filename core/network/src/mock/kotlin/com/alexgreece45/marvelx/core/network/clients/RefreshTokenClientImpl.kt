package com.alexgreece45.marvelx.core.network.clients

import com.alexgreece45.marvelx.core.network.constants.TokenConstants
import com.alexgreece45.marvelx.core.network.model.api.token.ResponseToken
import org.koin.core.annotation.Single
import retrofit2.Response

@Single
internal class RefreshTokenClientImpl : RefreshTokenClient {

    override suspend fun refreshAccessToken(
        clientId: String,
        refreshToken: String
    ): Response<ResponseToken> = Response.success(
        200,
        TokenConstants.responseToken
    )
}