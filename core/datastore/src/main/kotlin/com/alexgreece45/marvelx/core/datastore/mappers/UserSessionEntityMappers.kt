package com.alexgreece45.marvelx.core.datastore.mappers

import com.alexgreece45.marvelx.core.datastore.model.UserSessionEntity
import com.alexgreece45.marvelx.model.model.UserSession

internal fun UserSessionEntity.toModel(): UserSession =
    UserSession(
        userId = userId,
        authenticationTokens = authenticationTokens.toModel()
    )

internal fun UserSession.toEntity(): UserSessionEntity =
    UserSessionEntity(
        userId = userId,
        authenticationTokens = authenticationTokens.toEntity()
    )