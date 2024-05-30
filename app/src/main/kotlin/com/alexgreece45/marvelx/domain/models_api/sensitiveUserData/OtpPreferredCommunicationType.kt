package com.alexgreece45.marvelx.domain.models_api.sensitiveUserData

import androidx.annotation.StringRes
import com.alexgreece45.marvelx.R

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