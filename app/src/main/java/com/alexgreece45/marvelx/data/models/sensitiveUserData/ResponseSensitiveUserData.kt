package com.alexgreece45.marvelx.data.models.sensitiveUserData

import com.alexgreece45.marvelx.data.domains.sensitiveUserData.OtpPreferredCommunicationType
import com.alexgreece45.marvelx.data.domains.sensitiveUserData.SensitiveUserDataDomain
import com.alexgreece45.marvelx.util.extensions.maskEmail
import com.alexgreece45.marvelx.util.extensions.maskGreekPhoneNumber
import com.google.gson.annotations.SerializedName

/**
 * Defines the response of a sensitive user data request.
 *
 * Only used for displaying masked emails / phone numbers
 * to the screen for requesting new OTPs.
 *
 * @param type                  Refers to the type of the retrieved sensitive
 * user data (i.e., email, or phone_number).
 * @param data                  Refers to the value of the retrieved sensitive
 * user data (i.e., the stored email address, or phone number).
 * @param isOtpBlocked          Defines whether the `OTP` functionality is blocked.
 * @param otpBlockExpiresIn     Defines the remaining amount of time in seconds, from now, until the
 * `OTP` functionality is enabled so that new OTPs can be requested.
 */
data class ResponseSensitiveUserData(
    @SerializedName("type") val type: String,
    @SerializedName("data") val data: String,
    @SerializedName("isOtpBlocked") val isOtpBlocked: Boolean,
    @SerializedName("otpBlockExpiresIn") val otpBlockExpiresIn: Long?
)

fun ResponseSensitiveUserData.toSensitiveUserDataDomain(): SensitiveUserDataDomain =
    SensitiveUserDataDomain(
        otpPreferredCommunicationType = OtpPreferredCommunicationType.of(this.type),
        data = this.data,
        maskedData = this.data.toMaskedData(type = this.type),
        isOtpBlocked = this.isOtpBlocked,
        otpBlockExpiresIn = otpBlockExpiresIn
    )

private fun String.toMaskedData(type: String): String =
    when (type) {
        OtpPreferredCommunicationType.Email.key -> this.maskEmail()
        OtpPreferredCommunicationType.PhoneNumber.key -> this.maskGreekPhoneNumber()
        else -> this
    }
