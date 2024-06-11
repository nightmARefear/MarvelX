package com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails

import com.google.gson.annotations.SerializedName

data class ResponseProfileDetails(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
)
