package com.alexgreece45.marvelx.core.network.model.api.user.postLogin

import com.google.gson.annotations.SerializedName

data class RequestPostLogin(
    @SerializedName("accessToken") val accessToken: String
)
