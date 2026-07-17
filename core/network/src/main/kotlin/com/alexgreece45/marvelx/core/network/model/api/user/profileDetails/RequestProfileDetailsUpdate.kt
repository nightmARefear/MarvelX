package com.alexgreece45.marvelx.core.network.model.api.user.profileDetails

import com.google.gson.annotations.SerializedName

data class RequestProfileDetailsUpdate(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
)
