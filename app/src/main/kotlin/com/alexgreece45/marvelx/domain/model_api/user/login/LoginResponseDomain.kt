package com.alexgreece45.marvelx.domain.model_api.user.login

import com.alexgreece45.marvelx.core.network.model.api.user.login.ResponseLogin
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep

data class LoginResponseDomain(
    val verificationStep: VerificationStep,
    val userId: String
)

fun ResponseLogin.toUserLoginResponseDomain(): LoginResponseDomain =
    LoginResponseDomain(
        verificationStep = VerificationStep.of(this.verificationStep),
        userId = this.userId
    )