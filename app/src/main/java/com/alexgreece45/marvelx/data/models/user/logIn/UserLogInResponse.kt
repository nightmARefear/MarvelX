package com.alexgreece45.marvelx.data.models.user.logIn

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.domains.user.UserLogInResponseDomain
import com.google.gson.annotations.SerializedName

data class UserLogInResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("verification_step") val verificationStep: String,
    @SerializedName("epoch") val epoch: Long,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_token") val userToken: String?
)

fun UserLogInResponse.toDomain(): UserLogInResponseDomain =
    UserLogInResponseDomain(
        code = this.code,
        verificationStep = VerificationStep.of(this.verificationStep),
        epoch = this.epoch,
        userId = this.userId,
        userToken = this.userToken
    )
