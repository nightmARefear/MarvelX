package com.alexgreece45.marvelx.data.models.sensitiveUserData

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestSensitiveUserData(
    @SerializedName("jwt") val jwt: String,
    @SerializedName("verification_step") val verificationStep: String
) : Parcelable