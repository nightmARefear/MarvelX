package com.alexgreece45.marvelx.core.network.model.api.sensitiveUserData

import com.google.gson.annotations.SerializedName

data class RequestSensitiveUserData(
    @SerializedName("token") val token: String
)