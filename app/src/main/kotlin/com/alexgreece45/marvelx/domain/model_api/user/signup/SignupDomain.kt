package com.alexgreece45.marvelx.domain.model_api.user.signup

import com.alexgreece45.marvelx.network_logic.models_api.user.signup.ResponseSignup
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep

/**
 * @param verificationStep  The verification step. If it's [VerificationStep.None] then [tempAuthCode]
 * mustn't be blank.
 * @param userId            The user ID.
 * @param tempAuthCode      The temporary auth code to retrieve the auth code.
 */
data class SignupDomain(
    val verificationStep: VerificationStep,
    val userId: Int,
    val tempAuthCode: String
)

fun ResponseSignup.toSignupDomain(): SignupDomain =
    SignupDomain(
        verificationStep = VerificationStep.of(this.verificationStep),
        userId = this.userId.toInt(),
        tempAuthCode = this.tempAuthCode
    )
