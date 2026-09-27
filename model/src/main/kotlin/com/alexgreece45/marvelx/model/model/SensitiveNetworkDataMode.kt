package com.alexgreece45.marvelx.model.model

/**
 * How the network inspector (Chucker, in debug/mock builds) treats sensitive data: credentials in
 * headers (`Authorization`, `X-Auth-Signature`) and secret body values (tokens, the PKCE
 * `code_verifier`, the authorization `code`, `temp_auth_code`, passwords).
 */
enum class SensitiveNetworkDataMode {

    /** Every request is captured, with sensitive values replaced by a mask. */
    MASKED,

    /**
     * Every request is captured as is, sensitive values included. The default, so that every call
     * (including the ones made before logging in) can be inspected while debugging.
     */
    UNMASKED,

    /**
     * Requests to the token endpoint (authorization code exchange, token refresh) aren't captured
     * at all; every other request is captured masked.
     */
    HIDDEN;

    companion object {
        val default = UNMASKED
    }
}
