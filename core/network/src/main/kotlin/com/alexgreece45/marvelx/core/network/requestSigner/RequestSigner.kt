package com.alexgreece45.marvelx.core.network.requestSigner

import android.util.Base64
import com.alexgreece45.marvelx.core.common.constants.ConstantsSecurity
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.network.extensions.toJsonString
import com.alexgreece45.marvelx.core.network.signatureEngine.SignatureEngine
import com.alexgreece45.marvelx.core.network.storage.SignatureStorage
import okhttp3.Request
import org.koin.core.annotation.Single
import java.security.MessageDigest
import java.security.SecureRandom

internal interface RequestSigner {

    /**
     * Adds the necessary authentication headers to the given [okhttp3.Request.Builder] and returns the
     * finalized builder.
     */
    fun addAuthenticationHeaders(requestBuilder: Request.Builder): Request.Builder
}

@Single
internal class RequestSignerImpl(
    private val logger: AppLogger,
    private val signatureEngine: SignatureEngine,
    private val signatureStorage: SignatureStorage
) : RequestSigner {

    private val loggerTag = "RequestSigner"

    override fun addAuthenticationHeaders(
        requestBuilder: Request.Builder
    ): Request.Builder {
        val request = requestBuilder.build()
        val nonce = createNonce()
        val timestamp = DateUtil.getEpochSeconds()

        val xAuthSignature = signatureStorage.getSignature()?.let { signData ->
            val requestUrl = request.url
            signatureEngine.createSessionSignature(
                requestMethod = request.method,
                requestUrl = requestUrl,
                requestBody = request.body.toJsonString(),
                requestHeaders = request.headers,
                nonce = nonce,
                timestamp = timestamp,
                signature = signData
            )
        }.orEmpty()

        logger.d(loggerTag) { "(createSessionSignature) xAuthSignature: $xAuthSignature" }

        return requestBuilder
            .addHeader(
                name = ConstantsSecurity.HeaderName.XAuthNonce.name,
                value = nonce
            )
            .addHeader(
                name = ConstantsSecurity.HeaderName.XAuthTimestamp.name,
                value = timestamp.toString()
            )
            .addHeader(
                name = ConstantsSecurity.HeaderName.XAuthSignature.name,
                value = xAuthSignature
            )
    }

    /**
     * Creates a secure nonce using [SecureRandom], hashes it with `SHA-256` and returns the result
     * as a [String] in `Base64` encoding, suitable for HTTP requests.
     */
    private fun createNonce(): String {
        val bytes = ByteArray(64)
        SecureRandom().apply {
            nextBytes(bytes)
        }
        val messageDigest = MessageDigest.getInstance("SHA-256")
        messageDigest.update(bytes, 0, bytes.size)
        return Base64.encodeToString(
            messageDigest.digest(),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        )
    }
}