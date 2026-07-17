package com.alexgreece45.marvelx.core.common.config.model

/**
 * Timeout data for the `OkHttpClient` builder.
 *
 * @param connectTimeout    The connect timeout in seconds.
 * @param readTimeout       The read timeout in seconds.
 */
data class TimeoutData(
    val connectTimeout: Long,
    val readTimeout: Long
)