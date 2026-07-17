package com.alexgreece45.marvelx.core.network.model.api.user.profileDetails

import com.google.gson.annotations.SerializedName

/**
 * The error response of the get profile details request.
 */
data class ResponseProfileDetailsError(
    @SerializedName("getProfileDetailsErrorType") val getProfileDetailsErrorType: String
)
