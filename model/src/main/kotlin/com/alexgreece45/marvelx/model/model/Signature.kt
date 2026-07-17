package com.alexgreece45.marvelx.model.model

/**
 * Contains the signing data for requests.
 *
 * @param key       The signing key. The [String] representation of a `X.509` certificate.
 * @param algorithm The signing algorithm. The [String] representation of a hashing algorithm such
 * as `HmacSHA256`.
 */
data class Signature(
    val key: String,
    val algorithm: String
)