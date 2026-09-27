package com.alexgreece45.marvelx.controllers

import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64

/**
 * PKCE (RFC 7636) primitives. Uses `java.util.Base64` rather than `android.util.Base64` so it runs
 * (and is unit-testable) on the plain JVM.
 */
internal object Pkce {

    private const val RANDOM_TOKEN_BYTE_COUNT = 64

    private val secureRandom = SecureRandom()
    private val base64UrlEncoder = Base64.getUrlEncoder().withoutPadding()

    /**
     * Returns a cryptographically random, base64url-encoded (no padding) token. With 64 random bytes
     * it is 86 characters of `[A-Za-z0-9_-]`, within RFC 7636's 43–128 unreserved-character bound.
     * Used for both the `code_verifier` and the `state`.
     */
    fun createRandomUrlSafeToken(): String {
        val bytes = ByteArray(RANDOM_TOKEN_BYTE_COUNT)
        secureRandom.nextBytes(bytes)
        return base64UrlEncoder.encodeToString(bytes)
    }

    /** Returns `BASE64URL(SHA256(ASCII(verifier)))`, without padding. */
    fun createCodeChallenge(verifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(verifier.toByteArray(Charsets.US_ASCII))
        return base64UrlEncoder.encodeToString(digest)
    }
}
