package com.alexgreece45.marvelx.core.network.model.api.user.signup

import com.alexgreece45.marvelx.core.network.model.api.token.ResponseToken
import com.google.gson.annotations.SerializedName

data class ResponseSignup(
    @SerializedName("verificationStep") val verificationStep: String,
    @SerializedName("userId") val userId: String,

    /**
     * The user's tokens. Present only when no verification step is required (i.e., the
     * [verificationStep] is `none`). Otherwise, they are retrieved from `validate_otp`.
     */
    @SerializedName("tokens") val tokens: ResponseToken?
)
