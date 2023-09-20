package com.alexgreece45.marvelx.data.models.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserLogInRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
) : Parcelable
