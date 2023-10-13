package com.alexgreece45.marvelx.data.models.user.signUp

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.google.gson.annotations.SerializedName

data class UserSignupResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("success_step") val verificationStep: VerificationStep,
    @SerializedName("epoch") val epoch: Long
)
