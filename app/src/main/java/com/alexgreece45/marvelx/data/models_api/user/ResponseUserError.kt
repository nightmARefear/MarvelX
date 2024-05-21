package com.alexgreece45.marvelx.data.models_api.user

import com.google.gson.annotations.SerializedName

/**
 * Error class for all erroneous responses of MarvelX's - Backend API.
 * */
data class ResponseUserError(
    @SerializedName("error_code") val errorCode: String?,
    @SerializedName("error_message") val errorMessage: String
)