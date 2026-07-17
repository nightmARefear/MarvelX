package com.alexgreece45.marvelx.core.network.model.websocket

import com.google.gson.annotations.SerializedName

data class ResponseWebSocketDeleteNotification(
    @SerializedName("notificationId") val notificationId: Int
)
