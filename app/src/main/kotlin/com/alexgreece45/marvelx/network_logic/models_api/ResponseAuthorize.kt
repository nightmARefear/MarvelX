package com.alexgreece45.marvelx.network_logic.models_api

import com.google.gson.annotations.SerializedName

data class ResponseAuthorize(
    @SerializedName("authCode") val authCode: String
)