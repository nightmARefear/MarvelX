package com.alexgreece45.marvelx.core.datastore.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserSessionEntity(
    @SerialName("userId") val userId: String,
    @SerialName("authenticationTokens") val authenticationTokens: AuthenticationTokensEntity
)