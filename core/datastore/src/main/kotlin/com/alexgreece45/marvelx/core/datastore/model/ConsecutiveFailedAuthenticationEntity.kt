package com.alexgreece45.marvelx.core.datastore.model

import com.alexgreece45.marvelx.core.datastore.serializers.kotlin.InstantSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

/**
 * Data class representing information about consecutive failed authentication attempts.
 * This data should be initialized to `null` when the app first launches and updated whenever an
 * authentication attempt fails.
 *
 * @property timestamp  The timestamp of the most recent failed authentication attempt.
 * @property retries    The current number of consecutive failed authentication attempts.
 */
@Serializable
internal data class ConsecutiveFailedAuthenticationEntity(
    @Serializable(with = InstantSerializer::class)
    @SerialName("timestamp")
    val timestamp: Instant,

    @SerialName("retries") val retries: Int
)