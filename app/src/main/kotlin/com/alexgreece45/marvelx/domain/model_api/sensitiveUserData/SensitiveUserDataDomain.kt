package com.alexgreece45.marvelx.domain.model_api.sensitiveUserData

import com.alexgreece45.marvelx.core.network.model.api.sensitiveUserData.ResponseSensitiveUserData
import com.alexgreece45.marvelx.util.extensions.maskEmail
import com.alexgreece45.marvelx.util.extensions.maskGreekPhoneNumber

/**
 * Defines the initial required data needed in OTP-related operations.
 *
 * This data class is used to display the remaining block time until a new OTP can be requested,
 * the masked data (i.e., email or phone number), and it also provides [otpInterval] that defines
 * how often a new OTP can be requested.
 *
 * @param otpPreferredCommunicationType     Refers to the preferred communication type of the user
 * (i.e., email, or phone_number).
 * @param maskedData                        The masked value of the retrieved sensitive user data
 * (i.e., the stored email address, or phone number).
 * @param otpInterval                       The amount of seconds until a new OTP can be requested.
 * This is set when a new OTP has just been generated. It should be used to control the "Resend OTP"
 * functionality.
 * @param isOtpBlocked                      Defines whether the `OTP` functionality is blocked.
 * @param isResendOtpBlocked                Defines whether the `Resend OTP` functionality is blocked.
 * @param otpBlockExpiresIn                 Defines the remaining amount of time in seconds,
 * from now, until the `OTP` functionality is re-enabled so that new OTPs can be requested.
 */
data class SensitiveUserDataDomain(
    val otpPreferredCommunicationType: OtpPreferredCommunicationType,
    val maskedData: String,
    val otpInterval: Long,
    val isOtpBlocked: Boolean,
    val isResendOtpBlocked: Boolean,
    val otpBlockExpiresIn: Long?
)

fun ResponseSensitiveUserData.toSensitiveUserDataDomain(): SensitiveUserDataDomain =
    SensitiveUserDataDomain(
        otpPreferredCommunicationType = OtpPreferredCommunicationType.of(this.type),
        maskedData = this.data.toMaskedData(type = this.type),
        otpInterval = this.otpInterval,
        isOtpBlocked = this.isOtpBlocked,
        isResendOtpBlocked = isResendOtpBlocked,
        otpBlockExpiresIn = otpBlockExpiresIn
    )

private fun String.toMaskedData(type: String): String =
    when (type) {
        OtpPreferredCommunicationType.Email.key -> this.maskEmail()
        OtpPreferredCommunicationType.PhoneNumber.key -> this.maskGreekPhoneNumber()
        else -> this
    }