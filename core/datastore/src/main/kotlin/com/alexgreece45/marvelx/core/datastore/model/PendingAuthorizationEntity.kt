package com.alexgreece45.marvelx.core.datastore.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PendingAuthorizationEntity(
    @SerialName("state") val state: String,
    @SerialName("codeVerifier") val codeVerifier: String,
    @SerialName("redirectUri") val redirectUri: String,
    @SerialName("userId") val userId: String,
    @SerialName("shouldRememberUserSession") val shouldRememberUserSession: Boolean,
    @SerialName("createdAtEpochSeconds") val createdAtEpochSeconds: Long
)
