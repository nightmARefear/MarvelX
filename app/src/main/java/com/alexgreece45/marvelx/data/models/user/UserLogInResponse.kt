package com.alexgreece45.marvelx.data.models.user

import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.data.domains.user.UserLogInResponseDomain
import com.google.gson.annotations.SerializedName
import java.util.UUID

data class UserLogInResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("verification_step") val verificationStep: String,
    @SerializedName("epoch") val epoch: Long,
    @SerializedName("user_token") val userToken: String
)

fun UserLogInResponse.toDomain(): UserLogInResponseDomain =
    UserLogInResponseDomain(
        code = this.code,
        verificationStep = VerificationStep.of(this.verificationStep),
        epoch = epoch,
        userToken = UUID.fromString(this.userToken)
    )
