package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogic
import com.alexgreece45.marvelx.domain.models_api.user.postLogin.SignData
import okhttp3.Headers
import java.nio.charset.StandardCharsets
import java.util.UUID
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

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
     * @param nonce             The nonce to be used. It's a [UUID] that has been randomly generated.
     * @param timestamp         The timestamp to be used. It's a [Long] that represents the current
     * timestamp.
     * @param signData          The signing data to be used. It contains the key and the signature.
     *
     * @return                  The generated session signature, or an empty [String] if its
     * generation was unsuccessful.
     */
    fun createSessionSignature(
        requestMethod: String,
        requestUrl: String,
        requestBody: String,
        requestHeaders: Headers,
        nonce: UUID,
        timestamp: Long,
        signData: SignData
    ): String
}

class CryptoControllerImpl(
    private val appLogger: AppLoggerController,
    private val configLogic: ConfigLogic,
    private val configSecurityLogic: ConfigSecurityLogic
) : CryptoController {
    override fun createSessionSignature(
        requestMethod: String,
        requestUrl: String,
        requestBody: String,
        requestHeaders: Headers,
        nonce: UUID,
        timestamp: Long,
        signData: SignData
    ): String {
        val pathAndParameters = requestUrl.replace(configLogic.serverUrl, "").replace("/", ":")
        if (pathAndParameters.startsWith(":")) {
            pathAndParameters.replaceFirst(":", "")
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

        return try {
            val mac = Mac.getInstance(signData.algorithm)
            val keySpec =
                SecretKeySpec(signData.key.toByteArray(StandardCharsets.UTF_8), signData.algorithm)
            mac.init(keySpec)
            val signature = android.util.Base64.encodeToString(
                mac.doFinal(payload.toByteArray(StandardCharsets.UTF_8)),
                android.util.Base64.NO_WRAP
            )
//		  keySpec.destroy()
            signature
        } catch (e: Exception) {
            appLogger.e("CryptoController") { "(createSessionSignature) e: $e" }
            ""
        }
    }
}