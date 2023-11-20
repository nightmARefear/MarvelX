package com.alexgreece45.marvelx.data.domains.sensitiveUserData

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R
import com.alexgreece45.marvelx.util.extensions.maskEmail
import com.alexgreece45.marvelx.util.extensions.maskGreekPhoneNumber

data class SensitiveUserDataDomain(
    val otpPreferredCommunicationType: OtpPreferredCommunicationType,
    val data: String,
    val maskedData: String
)

sealed class OtpPreferredCommunicationType(
    open val key: String,
    @StringRes val label: Int
) {
    data object Email : OtpPreferredCommunicationType(
        key = "email",
        label = R.string.email
    )

    data object PhoneNumber : OtpPreferredCommunicationType(
        key = "phone_number",
        label = R.string.phone_number
    )

    data class Invalid(override val key: String) : OtpPreferredCommunicationType(key, label = 0)

    companion object {
        private fun asList(): List<OtpPreferredCommunicationType> = listOf(Email, PhoneNumber)

        fun of(value: String): OtpPreferredCommunicationType =
            asList().firstOrNull { it.key == value } ?: Invalid(value)

        fun isStringValidOtpPreferredCommunicationType(value: String): Boolean =
            of(value) != Invalid(value)
    }
}