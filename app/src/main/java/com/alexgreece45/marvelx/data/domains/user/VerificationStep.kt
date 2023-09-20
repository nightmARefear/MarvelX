package com.alexgreece45.marvelx.data.domains.user

sealed class VerificationStep(open val name: String) {
    object PinEmail : VerificationStep(name = "verification_pin_email")

    object PinSms : VerificationStep(name = "verification_pin_sms")

    object None : VerificationStep(name = "no_verification")

    data class Invalid(override val name: String) : VerificationStep(name)

    companion object {
        private fun asList(): List<VerificationStep> = listOf(PinEmail, PinSms, None)

        fun of(value: String): VerificationStep =
            asList().firstOrNull { it.name == value } ?: Invalid(value)
    }
}
