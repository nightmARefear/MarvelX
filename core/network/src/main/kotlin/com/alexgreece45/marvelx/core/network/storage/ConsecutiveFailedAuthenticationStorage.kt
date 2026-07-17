package com.alexgreece45.marvelx.core.network.storage

import com.alexgreece45.marvelx.model.model.ConsecutiveFailedAuthentication

/**
 * Stores data about consecutive failed authentication attempts, including the timestamp
 * of the most recent failed attempt and the current number of consecutive failures.
 *
 * This should be initialized to `null` when the app first starts.
 */
interface ConsecutiveFailedAuthenticationStorage {
    fun getConsecutiveFailedAuthentication(): ConsecutiveFailedAuthentication?
    fun setConsecutiveFailedAuthentication(consecutiveFailedAuthentication: ConsecutiveFailedAuthentication)
    fun removeConsecutiveFailedAuthentication()
}