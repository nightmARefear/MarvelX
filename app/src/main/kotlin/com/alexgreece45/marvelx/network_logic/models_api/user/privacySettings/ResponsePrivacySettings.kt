package com.alexgreece45.marvelx.network_logic.models_api.user.privacySettings

import com.google.gson.annotations.SerializedName

data class ResponsePrivacySettings(
    @SerializedName("isFriendsListVisible") val isFriendsListVisible: Boolean,
    @SerializedName("isListingsListVisible") val isListingsListVisible: Boolean,
    @SerializedName("isFavoritesListVisible") val isFavoritesListVisible: Boolean,
    @SerializedName("isGenderVisible") val isGenderVisible: Boolean,
    @SerializedName("isEmailVisible") val isEmailVisible: Boolean
)