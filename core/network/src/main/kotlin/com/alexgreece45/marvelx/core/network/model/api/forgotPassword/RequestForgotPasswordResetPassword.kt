package com.alexgreece45.marvelx.core.network.model.api.forgotPassword

import com.google.gson.annotations.SerializedName

/**
 * Sets a new password once the OTP for [requestId] has been validated.
 *
 * @param newPassword Already hashed client-side exactly as at signup/login, so the stored value
 * keeps matching what login sends.
 */
data class RequestForgotPasswordResetPassword(
    @SerializedName("requestId") val requestId: String,
    @SerializedName("verificationStep") val verificationStep: String,
    @SerializedName("newPassword") val newPassword: String
)
