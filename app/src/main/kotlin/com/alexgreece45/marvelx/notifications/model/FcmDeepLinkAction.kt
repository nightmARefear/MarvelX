package com.alexgreece45.marvelx.notifications.model

import com.alexgreece45.marvelx.data.model.deepLink.DeepLinkAction
import com.google.gson.annotations.SerializedName

data class FcmDeepLinkAction(
    @SerializedName("notificationId") val notificationId: Int,
    @SerializedName("deepLinkAction") val deepLinkAction: DeepLinkAction
)
