package com.alexgreece45.marvelx.core.network.model.api.verification_step

sealed class ResponseVerificationStep(open val key: String) {
    data object None : ResponseVerificationStep(key = "no_verification")
    data object OtpEmail : ResponseVerificationStep(key = "verification_pin_email")
    data object OtpPhoneNumber : ResponseVerificationStep(key = "verification_pin_sms")
    data class Invalid(override val key: String) : ResponseVerificationStep(key)

    companion object {
        private val validVerificationSteps = listOf(None, OtpEmail, OtpPhoneNumber)

        fun of(key: String): ResponseVerificationStep =
            validVerificationSteps.firstOrNull { it.key == key } ?: Invalid(key)
    }
}