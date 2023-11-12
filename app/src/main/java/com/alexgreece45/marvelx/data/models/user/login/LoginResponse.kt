package com.alexgreece45.marvelx.data.models.user.login

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.domains.user.login.LoginResponseDomain
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
