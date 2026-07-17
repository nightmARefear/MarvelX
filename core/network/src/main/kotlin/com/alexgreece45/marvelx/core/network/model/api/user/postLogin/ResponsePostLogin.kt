package com.alexgreece45.marvelx.core.network.model.api.user.postLogin

import com.google.gson.annotations.SerializedName

data class ResponsePostLogin(
    @SerializedName("sign") val sign: Sign? = null
)

/**
 * The signing data.
 *
 * @param key       The signing key. It is a X.509 Certificate.
 * @param algorithm The signing algorithm (e.g., `HmacSHA256`).
 */
data class Sign(
    @SerializedName("key") val key: String? = null,
    @SerializedName("algorithm") val algorithm: String? = null
)
