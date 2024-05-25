package com.alexgreece45.marvelx.data.models_api.user.signup

import com.alexgreece45.marvelx.domain.models_api.user.VerificationStep
import com.alexgreece45.marvelx.domain.models_api.user.signup.SignupResponseDomain
import com.google.gson.annotations.SerializedName

data class ResponseSignup(
    @SerializedName("verification_step") val verificationStep: String,
    @SerializedName("user_id") val userId: String
)

fun ResponseSignup.toSignupResponseDomain(): SignupResponseDomain =
    SignupResponseDomain(
        verificationStep = VerificationStep.of(this.verificationStep),
        userId = this.userId.toInt()
    )
