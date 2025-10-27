package com.alexgreece45.marvelx.network_logic.models_api.user.login

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestUserLogin(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("isLoggedInWithFirebase") val isLoggedInWithFirebase: Boolean
) : Parcelable
