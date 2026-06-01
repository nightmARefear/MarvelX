package com.alexgreece45.marvelx.core.network.model.api.otp

import com.google.gson.annotations.SerializedName

/**
 * Defines the error response of an OTP request.
 *
 * @param errorCause        The OTP error cause (i.e., OTP is incorrect, OTP is of invalid length,
 * OTP is expired, etc.).
 * @param blockExpiresIn    The number of seconds, from this time, until the client can request for
 * a new OTP.
 * */
data class OtpResponseError(
    @SerializedName("otp_error_cause") val errorCause: String,
    @SerializedName("block_expires_in") val blockExpiresIn: Long? = null,
)