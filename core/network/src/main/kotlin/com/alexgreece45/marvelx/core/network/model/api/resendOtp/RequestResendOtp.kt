package com.alexgreece45.marvelx.core.network.model.api.resendOtp

import com.google.gson.annotations.SerializedName

data class RequestResendOtp(
    @SerializedName("user_id") val userId: String,
    @SerializedName("verification_step") val verificationStep: String
)