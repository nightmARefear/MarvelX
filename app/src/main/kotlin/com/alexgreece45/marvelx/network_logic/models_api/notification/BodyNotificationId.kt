package com.alexgreece45.marvelx.network_logic.models_api.notification

import com.google.gson.annotations.SerializedName

data class BodyNotificationId(
    @SerializedName("notificationId") val notificationId: Int
)
