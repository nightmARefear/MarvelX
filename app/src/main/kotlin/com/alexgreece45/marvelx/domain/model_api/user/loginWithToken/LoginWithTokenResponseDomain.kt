package com.alexgreece45.marvelx.domain.model_api.user.loginWithToken

import com.alexgreece45.marvelx.core.network.model.api.user.loginWithToken.ResponseLoginWithToken
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep

data class LoginWithTokenResponseDomain(
    val verificationStep: VerificationStep
)

fun ResponseLoginWithToken.toUserLoginWithTokenResponseDomain(): LoginWithTokenResponseDomain =
    LoginWithTokenResponseDomain(
        verificationStep = VerificationStep.of(this.verificationStep)
    )