package com.alexgreece45.marvelx.data.models.user.logIn

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.domains.user.login.UserLogInResponseDomain
import com.google.gson.annotations.SerializedName

data class UserLoginResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("verification_step") val verificationStep: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_token") val userToken: String?
)

fun UserLoginResponse.toDomain(): UserLogInResponseDomain =
    UserLogInResponseDomain(
        code = this.code,
        verificationStep = VerificationStep.of(this.verificationStep),
        userId = this.userId,
        userToken = this.userToken
    )
