package com.alexgreece45.marvelx.core.datastore.mappers

import com.alexgreece45.marvelx.core.datastore.model.AuthenticationTokensEntity
import com.alexgreece45.marvelx.model.model.AuthenticationTokens

internal fun AuthenticationTokensEntity.toModel(): AuthenticationTokens = AuthenticationTokens(
    accessToken = accessToken,
    accessExpiresIn = accessExpiresIn,
    refreshToken = refreshToken,
    refreshExpiresIn = refreshExpiresIn,
    tokenType = tokenType
)

internal fun AuthenticationTokens.toEntity(): AuthenticationTokensEntity = AuthenticationTokensEntity(
    accessToken = accessToken,
    accessExpiresIn = accessExpiresIn,
    refreshToken = refreshToken,
    refreshExpiresIn = refreshExpiresIn,
    tokenType = tokenType
)