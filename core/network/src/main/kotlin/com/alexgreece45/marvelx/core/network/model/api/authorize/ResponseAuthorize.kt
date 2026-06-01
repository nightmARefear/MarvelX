package com.alexgreece45.marvelx.core.network.model.api.authorize

import com.google.gson.annotations.SerializedName

data class ResponseAuthorize(
    @SerializedName("authCode") val authCode: String
)