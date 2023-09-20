package com.alexgreece45.marvelx.data.models.user

import com.google.gson.annotations.SerializedName

/**
 * Error class for all erroneous responses of MarvelX's - Backend API.
 * */
data class UserErrorResponse(
    @SerializedName("code") val errorCode: Int,
    @SerializedName("error_message") val errorMessage: String
)