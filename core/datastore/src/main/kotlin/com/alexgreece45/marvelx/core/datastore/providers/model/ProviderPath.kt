package com.alexgreece45.marvelx.core.datastore.providers.model

internal enum class ProviderPath(private val path: String) {
    USER_ID("user_id"),
    AUTHENTICATION_TOKENS("authentication_tokens"),
    SIGNATURE("signature"),
    CONSECUTIVE_FAILED_AUTHENTICATION("consecutive_failed_authentication"),
    AUTO_LOGOUT_TIMESTAMP("auto_logout_timestamp");

    val fileName = "$path.json"
}