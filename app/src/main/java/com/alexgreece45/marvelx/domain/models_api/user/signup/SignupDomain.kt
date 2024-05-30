package com.alexgreece45.marvelx.domain.models_api.user.signup

import com.alexgreece45.marvelx.data.models_api.user.signup.ResponseSignup
import com.alexgreece45.marvelx.domain.models_api.user.VerificationStep

/**
 * @param verificationStep  The verification step. If it's [VerificationStep.None] then [authCode]
 * mustn't be blank.
 * @param userId            The user ID.
 * @param authCode          The auth code to retrieve the tokens.
 */
data class SignupDomain(
    val verificationStep: VerificationStep,
    val userId: Int,
    val authCode: String
)

fun ResponseSignup.toSignupDomain(): SignupDomain =
    SignupDomain(
        verificationStep = VerificationStep.of(this.verificationStep),
        userId = this.userId.toInt(),
        authCode = this.authCode
    )
