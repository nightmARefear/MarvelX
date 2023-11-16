package com.alexgreece45.marvelx.data.domains.user

sealed class VerificationStep(open val name: String) {
    data object OtpEmail : VerificationStep(name = "verification_pin_email")

    data object OtpPhoneNumber : VerificationStep(name = "verification_pin_sms")

    data object None : VerificationStep(name = "no_verification")

    data class Invalid(override val name: String) : VerificationStep(name)

    companion object {
        private fun asList(): List<VerificationStep> = listOf(OtpEmail, OtpPhoneNumber, None)

        fun of(value: String): VerificationStep =
            asList().firstOrNull { it.name == value } ?: Invalid(value)
    }
}
