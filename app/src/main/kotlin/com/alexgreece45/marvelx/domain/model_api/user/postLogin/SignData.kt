package com.alexgreece45.marvelx.domain.model_api.user.postLogin

/**
 * The signing data domain.
 *
 * @param key       The signing key. The [String] representation of a `X.509` certificate.
 * @param algorithm The signing algorithm. The [String] representation of a hashing algorithm such
 * as `HmacSHA256`.
 */
data class SignData(
    val key: String,
    val algorithm: String
)
