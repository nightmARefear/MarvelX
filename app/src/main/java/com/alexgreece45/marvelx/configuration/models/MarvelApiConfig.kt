package com.alexgreece45.marvelx.configuration.models

/**
 * Defines the config data for the Marvel API.
 *
 * @param scheme            The scheme. Either `HTTP` or `HTTPS`.
 * @param host              The host. That is the domain. For example, `test.domain.com`.
 * @param endpointPrefix    The endpoint prefix. For example, `/v1/public/`.
 */
data class MarvelApiConfig(
    val scheme: String,
    val host: String,
    val endpointPrefix: String
) {
    /**
     * The URL that derives from [scheme] and [host] as a [String].
     */
    val url: String get() = "$scheme://$host"
}
