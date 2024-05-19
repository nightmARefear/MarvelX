package com.alexgreece45.marvelx.data.models.retrieve_timestamp

import com.google.gson.annotations.SerializedName

/**
 * The server's timestamp.
 */
data class ResponseRetrieveTimestamp(
    @SerializedName("timestamp") val timestamp: Int
)