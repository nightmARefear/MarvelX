package com.alexgreece45.marvelx.data.models.user.signup

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.domains.user.signup.SignupResponseDomain
import com.google.gson.annotations.SerializedName

data class SignupResponse(
    @SerializedName("verification_step") val verificationStep: String,
    @SerializedName("user_id") val userId: String
)

fun SignupResponse.toSignupResponseDomain(): SignupResponseDomain =
    SignupResponseDomain(
        verificationStep = VerificationStep.of(this.verificationStep),
        userId = this.userId.toInt()
    )
