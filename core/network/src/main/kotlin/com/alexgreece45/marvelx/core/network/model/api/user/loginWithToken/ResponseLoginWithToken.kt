package com.alexgreece45.marvelx.core.network.model.api.user.loginWithToken

import com.google.gson.annotations.SerializedName

data class ResponseLoginWithToken(
    @SerializedName("verification_step") val verificationStep: String
)