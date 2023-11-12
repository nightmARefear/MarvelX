package com.alexgreece45.marvelx.data.models.user.loginWithToken

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.domains.user.loginWithToken.LoginWithTokenResponseDomain
import com.google.gson.annotations.SerializedName

data class LoginWithTokenResponse(
    @SerializedName("verification_step") val verificationStep: String
)

fun LoginWithTokenResponse.toUserLoginWithTokenResponseDomain(): LoginWithTokenResponseDomain =
    LoginWithTokenResponseDomain(
        verificationStep = VerificationStep.of(this.verificationStep)
    )
