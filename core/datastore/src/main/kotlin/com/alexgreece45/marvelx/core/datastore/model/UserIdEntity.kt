package com.alexgreece45.marvelx.core.datastore.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserIdEntity(
    @SerialName("userId") val userId: String
)