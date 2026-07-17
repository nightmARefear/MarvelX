package com.alexgreece45.marvelx.core.network.model.api.user.signup

import com.google.gson.annotations.SerializedName

data class RequestUserSignup(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
)