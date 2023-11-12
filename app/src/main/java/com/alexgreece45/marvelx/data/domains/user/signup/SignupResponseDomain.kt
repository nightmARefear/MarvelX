package com.alexgreece45.marvelx.data.domains.user.signup

import com.alexgreece45.marvelx.data.domains.user.VerificationStep

data class SignupResponseDomain(
    val verificationStep: VerificationStep,
    val userId: Int
)
