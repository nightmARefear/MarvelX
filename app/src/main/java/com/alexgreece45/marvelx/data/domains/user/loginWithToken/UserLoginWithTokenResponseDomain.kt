package com.alexgreece45.marvelx.data.domains.user.loginWithToken

import com.alexgreece45.marvelx.data.domains.user.VerificationStep

data class UserLoginWithTokenResponseDomain(
    val verificationStep: VerificationStep,
    val token: String?
)
