package com.alexgreece45.marvelx.data.models.user.loginWithToken

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserLoginWithTokenRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("epoch") val epoch: Long
) : Parcelable