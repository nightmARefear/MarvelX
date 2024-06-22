package com.alexgreece45.marvelx.network_logic.models_api.user

import com.alexgreece45.marvelx.network_logic.models_api.user.profileDetails.ResponseBasicProfileDetails
import com.google.gson.annotations.SerializedName

data class ResponseUsersBlocked(
    @SerializedName("blockedUsers") val blockedUsers: List<ResponseBasicProfileDetails> // TODO: CHANGE TO SMALLER VERSION (userId, fullName, profileImageUrl)
)
