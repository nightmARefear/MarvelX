package com.alexgreece45.marvelx.data.models_api.user.postLogin

import com.google.gson.annotations.SerializedName

data class RequestPostLogin(
    @SerializedName("accessToken") val accessToken: String
)
