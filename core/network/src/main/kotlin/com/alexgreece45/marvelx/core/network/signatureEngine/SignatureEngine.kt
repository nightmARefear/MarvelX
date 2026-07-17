package com.alexgreece45.marvelx.core.network.signatureEngine

import android.util.Base64
import com.alexgreece45.marvelx.core.common.config.SecurityConfigLogic
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.network.signatureEngine.model.SecureSecretKeySpec
import com.alexgreece45.marvelx.model.model.Signature
import okhttp3.Headers
import okhttp3.HttpUrl
import org.koin.core.annotation.Single
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Mac

/**
 * Provides various functions for cryptography-related operations such as creating a session
 * signature for API calls, and many more.
 */
internal interface SignatureEngine {

    /**
     * Creates the session signature for API calls. The given parameters correspond to a single
     * request.
     *
     * @param requestMethod     The method of this request.
     * @param requestUrl        The URL of this request as a [String].
     * @param requestBody       The body of this request.
     * @param requestHeaders    The headers of this request as [okhttp3.Headers].
     * @param nonce             The nonce to be used. A randomly generated [String] that's SHA-256
     * hashed and encoded in Base64.
     * @param timestamp         The timestamp to be used. It's a [Long] that represents the current
     * timestamp.
     * @param signature          The signing data to be used. It contains the key and the signature.
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
        signature: Signature
    ): String


    // TODO: Extract to different interface.
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

@Single
internal class SignatureEngineImpl(
    private val appLogger: AppLogger,
    private val securityConfigLogic: SecurityConfigLogic
) : SignatureEngine {

    private val loggerTag = "SignatureEngine"

    override fun createSessionSignature(
        requestMethod: String,
        requestUrl: HttpUrl,
        requestBody: String,
        requestHeaders: Headers,
        nonce: String,
        timestamp: Long,
        signature: Signature
    ): String {
        var pathAndParameters = requestUrl.pathSegments.joinToString(separator = ":")
        if (pathAndParameters.startsWith(":")) {
            pathAndParameters = pathAndParameters.replaceFirst(":", "")
        }

        val queryParameters = when (val query = requestUrl.query) {
            null -> ""
            else -> "?$query"
        }
        pathAndParameters += queryParameters // TODO: Change in future to support "|" separator. Temporary solution since backend cannot implement this change yet.

        val headerSeparator = "\n"
        var headers = requestHeaders.toMultimap().toList()
            .joinToString(separator = headerSeparator) { header ->
                if (header.first in securityConfigLogic.sessionSignatureHeaders) {
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
        appLogger.d(loggerTag) { "payload: $payload" }

        return try {
            val signatureAlgorithm = signature.algorithm

            val mac = Mac.getInstance(signatureAlgorithm)
            val keySpec =
                SecureSecretKeySpec(signature.key.toByteArray(Charsets.UTF_8), signatureAlgorithm)
            mac.init(keySpec)
            val signature = Base64.encodeToString(
                mac.doFinal(payload.toByteArray(Charsets.UTF_8)),
                Base64.NO_WRAP
            )
            keySpec.destroy()
            signature
        } catch (e: Exception) {
            appLogger.e(loggerTag) { "(createSessionSignature) e: $e" }
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