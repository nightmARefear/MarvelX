package com.alexgreece45.marvelx.data.models.user.login

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.domains.user.login.UserLoginResponseDomain
import com.google.gson.annotations.SerializedName

data class UserLoginResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("verification_step") val verificationStep: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("user_token") val userToken: String?
)

fun UserLoginResponse.toDomain(): UserLoginResponseDomain =
    UserLoginResponseDomain(
        code = this.code,
        verificationStep = VerificationStep.of(this.verificationStep),
        userId = this.userId.toInt(),
        userToken = this.userToken
    )
