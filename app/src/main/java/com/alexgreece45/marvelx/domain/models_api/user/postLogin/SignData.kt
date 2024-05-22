package com.alexgreece45.marvelx.domain.models_api.user.postLogin

import com.alexgreece45.marvelx.data.models_api.user.postLogin.Sign

/**
 * The signing data domain.
 *
 * @param key       The signing key. It is a X.509 Certificate.
 * @param algorithm The signing algorithm (e.g., `HmacSHA256`).
 */
data class SignData(
    val key: String,
    val algorithm: String
)
