package com.alexgreece45.marvelx.core.network.model.api.notification

import com.google.gson.annotations.SerializedName

data class BodyNotificationId(
    @SerializedName("notificationId") val notificationId: Int
)
