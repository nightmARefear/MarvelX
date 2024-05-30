package com.alexgreece45.marvelx.data.models_api.user.login

import com.alexgreece45.marvelx.domain.models_api.user.VerificationStep
import com.alexgreece45.marvelx.domain.models_api.user.login.LoginResponseDomain
import com.google.gson.annotations.SerializedName

data class ResponseLogin(
    @SerializedName("verificationStep") val verificationStep: String,
    @SerializedName("userId") val userId: String
)

fun ResponseLogin.toUserLoginResponseDomain(): LoginResponseDomain =
    LoginResponseDomain(
        verificationStep = VerificationStep.of(this.verificationStep),
        userId = this.userId.toInt()
    )
