package com.alexgreece45.marvelx.data.domains.user

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class UserLogInResponseDomain(
    @SerializedName("code") val code: Int,
    @SerializedName("verification_step") val verificationStep: VerificationStep,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_token") val userToken: String?
)