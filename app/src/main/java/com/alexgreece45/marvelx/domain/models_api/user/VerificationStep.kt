package com.alexgreece45.marvelx.domain.models_api.user

/**
 * This sealed class represents the required verification step needed for an operation. This can be:
 *  - [None]; no actions are typically needed to be performed
 *  - [OtpEmail] or [OtpPhoneNumber]; actions are typically needed to be performed
 *  - [Invalid] the given [key] could not be mapped to a valid [VerificationStep]
 */
sealed class VerificationStep(open val key: String) {
    data object None: VerificationStep(key = "no_verification")
    data object OtpEmail : VerificationStep(key = "verification_pin_email")
    data object OtpPhoneNumber : VerificationStep(key = "verification_pin_sms")
    data class Invalid(override val key: String) : VerificationStep(key)

    companion object {
        /**
         * The [List] of all the valid [VerificationStep]s.
         */
        private val validVerificationSteps: List<VerificationStep> =
            listOf(None, OtpEmail, OtpPhoneNumber)

        /**
         * Retrieves the [VerificationStep] based on the given [key]. If the [key] could not be
         * mapped to a valid [VerificationStep] then [VerificationStep.Invalid] is returned,
         * alongside the key as its value.
         */
        fun of(key: String): VerificationStep =
            validVerificationSteps.firstOrNull { it.key == key } ?: Invalid(key)
    }
}