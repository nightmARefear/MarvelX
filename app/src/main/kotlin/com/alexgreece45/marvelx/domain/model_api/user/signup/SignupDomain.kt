package com.alexgreece45.marvelx.domain.model_api.user.signup

import com.alexgreece45.marvelx.core.network.model.api.token.toModel
import com.alexgreece45.marvelx.core.network.model.api.user.signup.ResponseSignup
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep
import com.alexgreece45.marvelx.model.model.AuthenticationTokens

/**
 * @param verificationStep      The verification step. If it's [VerificationStep.None] then
 * [authenticationTokens] mustn't be `null`.
 * @param userId                The user ID.
 * @param authenticationTokens  The user's tokens, when no verification step is required.
 */
data class SignupDomain(
    val verificationStep: VerificationStep,
    val userId: String,
    val authenticationTokens: AuthenticationTokens?
)

fun ResponseSignup.toSignupDomain(): SignupDomain =
    SignupDomain(
        verificationStep = VerificationStep.of(this.verificationStep),
        userId = this.userId,
        authenticationTokens = this.tokens?.toModel()
    )
