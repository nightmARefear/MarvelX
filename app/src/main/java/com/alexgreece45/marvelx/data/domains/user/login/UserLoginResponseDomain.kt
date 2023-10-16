package com.alexgreece45.marvelx.data.domains.user.login

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.google.gson.annotations.SerializedName

data class UserLoginResponseDomain(
    @SerializedName("code") val code: Int,
    @SerializedName("verification_step") val verificationStep: VerificationStep,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_token") val userToken: String?
)