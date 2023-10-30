package com.alexgreece45.marvelx.data.domains.user.login

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.google.gson.annotations.SerializedName

data class UserLoginResponseDomain(
    val code: Int,
    val verificationStep: VerificationStep,
    val userId: Int,
    val accessToken: String
)