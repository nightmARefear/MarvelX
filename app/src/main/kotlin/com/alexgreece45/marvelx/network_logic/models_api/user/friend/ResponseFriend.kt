package com.alexgreece45.marvelx.network_logic.models_api.user.friend

import com.google.gson.annotations.SerializedName

data class ResponseFriend(
    @SerializedName("userId") val userId: String,
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String
)
