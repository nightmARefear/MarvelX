package com.alexgreece45.marvelx.data.domains.user.login

import com.alexgreece45.marvelx.data.domains.user.VerificationStep

data class LoginResponseDomain(
    val verificationStep: VerificationStep,
    val userId: Int
)