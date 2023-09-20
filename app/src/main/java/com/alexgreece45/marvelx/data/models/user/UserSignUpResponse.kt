package com.alexgreece45.marvelx.data.models.user

import com.google.gson.annotations.SerializedName

data class UserSignUpResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("success_step") val successStep: SuccessStep,
    @SerializedName("epoch") val epoch: Long
)

sealed class SuccessStep(open val name: String) {
    object VerificationPinEmail : SuccessStep(name = "verification_pin_email")
    object VerificationPinSms : SuccessStep(name = "verification_pin_sms")

    data class Invalid(override val name: String) : SuccessStep(name)

    companion object {
        private fun asList(): List<SuccessStep> = listOf(VerificationPinEmail, VerificationPinSms)

        fun of(value: String): SuccessStep =
            asList().firstOrNull { it.name == value } ?: Invalid(value)
    }
}
