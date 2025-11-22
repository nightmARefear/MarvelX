package com.alexgreece45.marvelx.network_logic.models_websocket

import com.google.gson.annotations.SerializedName

data class ResponseWebSocketDeleteNotification(
    @SerializedName("notificationId") val notificationId: Int
)
