package com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings

import com.google.gson.annotations.SerializedName

data class ResponsePrivacySettings(
    @SerializedName("isGenderVisible") val isGenderVisible: Boolean,
    @SerializedName("isEmailVisible") val isEmailVisible: Boolean
)