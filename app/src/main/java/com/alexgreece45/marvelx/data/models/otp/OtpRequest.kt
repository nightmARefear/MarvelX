package com.alexgreece45.marvelx.data.models.otp

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class OtpRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("otp") val otp: Int,
    @SerializedName("epoch") val epoch: Long
) : Parcelable
