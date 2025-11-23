package com.alexgreece45.marvelx.network_logic.models_api.device

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RequestUpdateDevice(
    @SerializedName("fcmToken") val fcmToken: String?
)
