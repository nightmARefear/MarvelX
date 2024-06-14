package com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings

import com.google.gson.annotations.SerializedName

data class RequestPrivacySettings(
    @SerializedName("isGenderVisible") val isGenderVisible: String,
    @SerializedName("isEmailVisible") val isEmailVisible: String
)
