package com.alexgreece45.marvelx.domain.models_api.otp

sealed class OtpFlowType(open val key: String) {
    /**
     * Represents an OTP flow type of `Register`. That means that the previous screen was either
     * `LoginScreen` or `SignupScreen`.
     */
    data object Register : OtpFlowType(key = "register")

    /**
     * Represents an OTP flow type of `Forgot Password`. That means that the previous screen was
     * either `ForgotPasswordEnterEmailScreen` or `ForgotPasswordEnterPhoneNumberScreen`.
     */
    sealed class ForgotPassword(override val key: String) : OtpFlowType(key) {
        data object Email : ForgotPassword(key = "forgotPasswordEmail")
        data object PhoneNumber : ForgotPassword(key = "forgotPasswordPhoneNumber")
    }

    data class Invalid(override val key: String) : OtpFlowType(key)

    companion object {
        /**
         * The valid OTP flow types.
         */
        private fun validOtpFlowTypes(): List<OtpFlowType> =
            listOf(
                Register,
                ForgotPassword.Email,
                ForgotPassword.PhoneNumber
            )

        /**
         * Returns the [OtpFlowType] that matches the given [key]. If no such flow type was found,
         * [Invalid] with [key] is returned to signify an error.
         */
        fun getType(key: String): OtpFlowType =
            validOtpFlowTypes().firstOrNull { validOtpFlowType -> validOtpFlowType.key == key }
                ?: Invalid(key)
    }
}