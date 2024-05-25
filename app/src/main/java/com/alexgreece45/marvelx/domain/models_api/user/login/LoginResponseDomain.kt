package com.alexgreece45.marvelx.domain.models_api.user.login

import com.alexgreece45.marvelx.domain.models_api.user.VerificationStep

data class LoginResponseDomain(
    val verificationStep: VerificationStep,
    val userId: Int
)