package com.alexgreece45.marvelx.data.domains.sensitiveUserData

/**
 * Defines the initial required data needed in [OtpScreen].
 *
 * Only used for displaying masked emails / phone numbers
 * to the screen for requesting new OTPs.
 *
 * @param otpPreferredCommunicationType     Refers to the preferred communication type of the user
 * (i.e., email, or phone_number).
 * @param data                              Refers to the value of the retrieved sensitive user data
 * (i.e., the stored email address, or phone number).
 * @param isOtpBlocked                      Defines whether the `OTP` functionality is blocked.
 * @param otpBlockExpiresIn                 Defines the remaining amount of time in seconds,
 * from now, until the `OTP` functionality is enabled so that new OTPs can be requested.
 */
data class SensitiveUserDataDomain(
    val otpPreferredCommunicationType: OtpPreferredCommunicationType,
    val data: String,
    val maskedData: String,
    val isOtpBlocked: Boolean,
    val otpBlockExpiresIn: Long?
)