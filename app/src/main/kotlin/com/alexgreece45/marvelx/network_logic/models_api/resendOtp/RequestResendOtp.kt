package com.alexgreece45.marvelx.network_logic.models_api.resendOtp

import com.google.gson.annotations.SerializedName

data class RequestResendOtp(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("verification_step") val verificationStep: String
)