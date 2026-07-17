package com.alexgreece45.marvelx.core.network.model.api.user.block

import com.google.gson.annotations.SerializedName

/**
 * The response when blocking another user.
 */
data class ResponseUserBlockResult(
    @SerializedName("user_block_result") val userBlockResult: String
)
