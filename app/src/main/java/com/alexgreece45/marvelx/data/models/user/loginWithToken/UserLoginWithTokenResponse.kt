package com.alexgreece45.marvelx.data.models.user.loginWithToken

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.domains.user.loginWithToken.UserLoginWithTokenResponseDomain
import com.google.gson.annotations.SerializedName

data class UserLoginWithTokenResponse(
    @SerializedName("code") val code: String,
    @SerializedName("verification_step") val verificationStep: String,
    @SerializedName("token") val token: String?
)

fun UserLoginWithTokenResponse.toUserLoginWithTokenResponseDomain(): UserLoginWithTokenResponseDomain =
    UserLoginWithTokenResponseDomain(
        verificationStep = VerificationStep.of(this.verificationStep),
        token = this.token
    )
