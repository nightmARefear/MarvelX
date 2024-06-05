package com.alexgreece45.marvelx.domain.model_api.user.loginWithToken

import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep

data class LoginWithTokenResponseDomain(
    val verificationStep: VerificationStep
)
