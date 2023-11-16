package com.alexgreece45.marvelx.data.models.jwtSensitiveUserData

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestJwtSensitiveUserData(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("verification_step") val verificationStep: String
) : Parcelable