package com.alexgreece45.marvelx.data.models.user

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.google.gson.annotations.SerializedName

data class UserSignUpResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("success_step") val verificationStep: VerificationStep,
    @SerializedName("epoch") val epoch: Long
)
