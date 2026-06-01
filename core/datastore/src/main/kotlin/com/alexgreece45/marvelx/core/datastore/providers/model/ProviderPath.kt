package com.alexgreece45.marvelx.core.datastore.providers.model

internal enum class ProviderPath(private val path: String) {
    USER_SESSION("user_session"),
    SIGNATURE("signature"),
    CONSECUTIVE_FAILED_AUTHENTICATION("consecutive_failed_authentication"),
    AUTO_LOGOUT_TIMESTAMP("auto_logout_timestamp");

    val fileName = "$path.json"
}