package com.alexgreece45.marvelx.core.network.model.api.device

import com.google.gson.annotations.SerializedName

data class RequestRegisterDevice(
    @SerializedName("fcmToken") val fcmToken: String?
)