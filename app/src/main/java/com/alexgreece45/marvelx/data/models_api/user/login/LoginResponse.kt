package com.alexgreece45.marvelx.data.models_api.user.login

import com.alexgreece45.marvelx.domain.models_api.user.VerificationStep
import com.alexgreece45.marvelx.domain.models_api.user.login.LoginResponseDomain
import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("verification_step") val verificationStep: String,
    @SerializedName("user_id") val userId: String
)

fun LoginResponse.toUserLoginResponseDomain(): LoginResponseDomain =
    LoginResponseDomain(
        verificationStep = VerificationStep.of(this.verificationStep),
        userId = this.userId.toInt()
    )
