package com.alexgreece45.marvelx.controllers

import android.util.Base64
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogic
import com.alexgreece45.marvelx.controllers.model.crypto.SecureSecretKeySpec
import com.alexgreece45.marvelx.domain.model_api.user.postLogin.SignData
import okhttp3.Headers
import okhttp3.HttpUrl
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.Mac

/**
 * Provides various functions for cryptography-related operations such as creating a session
 * signature for API calls, and many more.
 */
interface CryptoController {
    /**
     * Creates the session signature for API calls. The given parameters correspond to a single
     * request.
     *
     * @param requestMethod     The method of this request.
     * @param requestUrl        The URL of this request as a [String].
     * @param requestBody       The body of this request.
     * @param requestHeaders    The headers of this request as [Headers].
     * @param nonce             The nonce to be used. A randomly generated [String] that's SHA-256
     * hashed and encoded in Base64.
     * @param timestamp         The timestamp to be used. It's a [Long] that represents the current
     * timestamp.
     * @param signData          The signing data to be used. It contains the key and the signature.
     *
     * @return                  The generated session signature, or an empty [String] if its
     * generation was unsuccessful.
     */
    fun createSessionSignature(
        requestMethod: String,
        requestUrl: HttpUrl,
        requestBody: String,
        requestHeaders: Headers,
        nonce: String,
        timestamp: Long,
        signData: SignData
    ): String

    /**
     * Creates and returns the code verifier used for the OAuth 2.0 Grant flow.
     */
    fun createCodeVerifier(): String

    /**
     * Creates and returns the code challenge with the given [verifier]. The hashing algorithm that
     * is used is `SHA-256`.
     */
    fun createCodeChallenge(verifier: String): String
}

class CryptoControllerImpl(
    private val appLogger: AppLogger,
    private val configSecurityLogic: ConfigSecurityLogic
) : CryptoController {
    override fun createSessionSignature(
        requestMethod: String,
        requestUrl: HttpUrl,
        requestBody: String,
        requestHeaders: Headers,
        nonce: String,
        timestamp: Long,
        signData: SignData
    ): String {
        var pathAndParameters = requestUrl.pathSegments.joinToString(separator = ":")
        if (pathAndParameters.startsWith(":")) {
            pathAndParameters = pathAndParameters.replaceFirst(":", "")
        }

        val headerSeparator = "\n"
        var headers = requestHeaders.toMultimap().toList()
            .joinToString(separator = headerSeparator) { header ->
                if (header.first in configSecurityLogic.sessionSignatureHeaders) {
                    header.first + ":" + header.second.first()
                } else {
                    ""
                }
            }
        headers = if (headers.startsWith(headerSeparator)) headers.replaceFirst(
            headerSeparator,
            ""
        ) else headers

        val payloadSeparator = "|"
        val payload = listOf(
            requestMethod,
            pathAndParameters,
            requestBody,
            headers,
            nonce,
            timestamp
        ).joinToString(payloadSeparator)
        appLogger.d("CryptoController") { "payload: $payload" }

        return try {
            val mac = Mac.getInstance(signData.algorithm)
            val keySpec =
                SecureSecretKeySpec(signData.key.toByteArray(Charsets.UTF_8), signData.algorithm)
            mac.init(keySpec)
            val signature = Base64.encodeToString(
                mac.doFinal(payload.toByteArray(Charsets.UTF_8)),
                Base64.NO_WRAP
            )
            keySpec.destroy()
            signature
        } catch (e: Exception) {
            appLogger.e("CryptoController") { "(createSessionSignature) e: $e" }
            ""
        }
    }

    override fun createCodeVerifier(): String {
        val code = ByteArray(64)
        SecureRandom().apply {
            nextBytes(code)
        }
        return Base64.encodeToString(code, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
    }

    override fun createCodeChallenge(verifier: String): String {
        val bytes = verifier.toByteArray(Charsets.UTF_8)
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes, 0, bytes.size)
        return Base64.encodeToString(
            messageDigest.digest(),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
    }
}