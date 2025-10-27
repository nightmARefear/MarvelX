package com.alexgreece45.marvelx.controllers.model.auth

import com.google.gson.annotations.SerializedName
import java.time.Instant

/**
 * Data class representing information about consecutive failed authentication attempts.
 * This data should be initialized to `null` when the app first launches and updated whenever an
 * authentication attempt fails.
 *
 * @property timestamp  The timestamp of the most recent failed authentication attempt.
 * @property retries    The current number of consecutive failed authentication attempts.
 */
data class CurrentConsecutiveFailedAuthenticationData(
    @SerializedName("timestamp") val timestamp: Instant,
    @SerializedName("retries") val retries: Int
)
