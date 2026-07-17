package com.alexgreece45.marvelx.core.datastore.mappers

import com.alexgreece45.marvelx.core.datastore.model.ConsecutiveFailedAuthenticationEntity
import com.alexgreece45.marvelx.model.model.ConsecutiveFailedAuthentication

internal fun ConsecutiveFailedAuthenticationEntity.toModel(): ConsecutiveFailedAuthentication =
    ConsecutiveFailedAuthentication(
        timestamp = timestamp,
        retries = retries
    )

internal fun ConsecutiveFailedAuthentication.toEntity(): ConsecutiveFailedAuthenticationEntity =
    ConsecutiveFailedAuthenticationEntity(
        timestamp = timestamp,
        retries = retries
    )