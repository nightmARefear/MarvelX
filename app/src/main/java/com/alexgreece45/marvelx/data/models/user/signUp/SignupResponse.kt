package com.alexgreece45.marvelx.data.models.user.signUp

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.google.gson.annotations.SerializedName

data class SignupResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("verification_step") val verificationStep: VerificationStep,
    @SerializedName("epoch") val epoch: Long
)
