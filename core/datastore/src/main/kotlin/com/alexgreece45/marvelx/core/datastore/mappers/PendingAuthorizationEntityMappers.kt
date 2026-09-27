package com.alexgreece45.marvelx.core.datastore.mappers

import com.alexgreece45.marvelx.core.datastore.model.PendingAuthorizationEntity
import com.alexgreece45.marvelx.model.model.PendingAuthorization

internal fun PendingAuthorizationEntity.toModel(): PendingAuthorization =
    PendingAuthorization(
        state = state,
        codeVerifier = codeVerifier,
        redirectUri = redirectUri,
        userId = userId,
        shouldRememberUserSession = shouldRememberUserSession,
        createdAtEpochSeconds = createdAtEpochSeconds
    )

internal fun PendingAuthorization.toEntity(): PendingAuthorizationEntity =
    PendingAuthorizationEntity(
        state = state,
        codeVerifier = codeVerifier,
        redirectUri = redirectUri,
        userId = userId,
        shouldRememberUserSession = shouldRememberUserSession,
        createdAtEpochSeconds = createdAtEpochSeconds
    )
