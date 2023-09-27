package com.alexgreece45.marvelx.data.models.user.logIn

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserLogInRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("epoch") val epoch: Long,
) : Parcelable
