package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
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
     * Creates a session signature.
     */
    fun createSessionSignature(
        requestMethod: String,
        requestUrl: String,
        requestBody: String,
        requestHeaders: Headers,
        nonce: UUID,
        timestamp: Long,
        key: String,
        algorithm: String
    ): String
}

class CryptoControllerImpl(
    private val configLogic: ConfigLogic
) : CryptoController {
    override fun createSessionSignature(
        requestMethod: String,
        requestUrl: String,
        requestBody: String,
        requestHeaders: Headers,
        nonce: UUID,
        timestamp: Long,
        key: String,
        algorithm: String
    ): String {
        val pathAndParameters = requestUrl.replace(configLogic.serverUrl, "").replace("/", ":")
        if (pathAndParameters.startsWith(":")) {
            pathAndParameters.replaceFirst(":", "")
        }

        val headerSeparator = "\n"
        var headers = requestHeaders.toMultimap().toList()
            .joinToString(separator = headerSeparator) { header ->
                if (header.first in listOf(ConstantsSecurity.HeaderName.UserAgent.name.lowercase())) {
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
        ).joinToString {
            payloadSeparator
        }

        return try {
            val mac = Mac.getInstance(algorithm)
            val keySpec = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), algorithm)
            mac.init(keySpec)
            val signature = android.util.Base64.encodeToString(
                mac.doFinal(payload.toByteArray(StandardCharsets.UTF_8)),
                android.util.Base64.NO_WRAP
            )
//		  keySpec.destroy()
            signature
        } catch (e: Exception) {
            ""
        }
    }

}