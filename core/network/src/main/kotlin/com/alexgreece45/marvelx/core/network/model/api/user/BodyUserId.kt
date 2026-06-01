package com.alexgreece45.marvelx.core.network.model.api.user

import com.google.gson.annotations.SerializedName

data class BodyUserId(
    @SerializedName("userId") val userId: String
)
