package com.alexgreece45.marvelx.core.datastore.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SignatureEntity(
    @SerialName("key") val key: String,
    @SerialName("algorithm") val algorithm: String
)
