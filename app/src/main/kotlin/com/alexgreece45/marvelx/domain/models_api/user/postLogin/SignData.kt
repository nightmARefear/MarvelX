package com.alexgreece45.marvelx.domain.models_api.user.postLogin

import com.alexgreece45.marvelx.network_logic.models_api.user.postLogin.Sign

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
