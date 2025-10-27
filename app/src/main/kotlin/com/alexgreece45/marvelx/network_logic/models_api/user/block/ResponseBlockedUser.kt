package com.alexgreece45.marvelx.network_logic.models_api.user.block

import com.google.gson.annotations.SerializedName

data class ResponseBlockedUser(
    @SerializedName("userId") val userId: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String,
    @SerializedName("blockedAt") val blockedAt: String
)
