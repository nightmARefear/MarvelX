package com.alexgreece45.marvelx.core.network.model.api.user.login

import com.google.gson.annotations.SerializedName

data class RequestUserLogin(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("isLoggedInWithFirebase") val isLoggedInWithFirebase: Boolean
)
