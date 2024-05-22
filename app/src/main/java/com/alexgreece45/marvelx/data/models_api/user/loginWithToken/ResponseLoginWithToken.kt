package com.alexgreece45.marvelx.data.models_api.user.loginWithToken

import com.alexgreece45.marvelx.domain.models_api.user.VerificationStep
import com.alexgreece45.marvelx.domain.models_api.user.loginWithToken.LoginWithTokenResponseDomain
import com.google.gson.annotations.SerializedName

data class ResponseLoginWithToken(
    @SerializedName("verification_step") val verificationStep: String
)

fun ResponseLoginWithToken.toUserLoginWithTokenResponseDomain(): LoginWithTokenResponseDomain =
    LoginWithTokenResponseDomain(
        verificationStep = VerificationStep.of(this.verificationStep)
    )
