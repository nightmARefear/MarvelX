package com.alexgreece45.marvelx.network_logic.models_api.sensitiveUserData

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestSensitiveUserData(
    @SerializedName("token") val token: String
) : Parcelable