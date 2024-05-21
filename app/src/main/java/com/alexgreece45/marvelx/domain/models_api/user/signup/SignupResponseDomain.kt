package com.alexgreece45.marvelx.domain.models_api.user.signup

import com.alexgreece45.marvelx.domain.models_api.user.VerificationStep

data class SignupResponseDomain(
    val verificationStep: VerificationStep,
    val userId: Int
)
