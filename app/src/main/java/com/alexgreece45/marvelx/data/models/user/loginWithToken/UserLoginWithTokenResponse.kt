package com.alexgreece45.marvelx.data.models.user.loginWithToken

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.domains.user.loginWithToken.UserLoginWithTokenResponseDomain
import com.google.gson.annotations.SerializedName

data class UserLoginWithTokenResponse(
    @SerializedName("verification_step") val verificationStep: String
)

fun UserLoginWithTokenResponse.toUserLoginWithTokenResponseDomain(): UserLoginWithTokenResponseDomain =
    UserLoginWithTokenResponseDomain(
        verificationStep = VerificationStep.of(this.verificationStep)
    )
