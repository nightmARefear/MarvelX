package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.SecurityUtil
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
import com.alexgreece45.marvelx.util.extensions.toJsonString
import okhttp3.Request
import java.util.UUID

interface RequestController {

    /**
     * Adds the necessary authentication headers to the given [Request.Builder] and returns the
     * finalized builder.
     */
    fun addAuthenticationHeaders(requestBuilder: Request.Builder): Request.Builder
}

class RequestControllerImpl(
    private val logger: AppLogger,
    private val cryptoController: CryptoController,
    private val prefKeys: PrefKeys
) : RequestController {

    override fun addAuthenticationHeaders(
        requestBuilder: Request.Builder
    ): Request.Builder {
        val request = requestBuilder.build()
        val nonce = SecurityUtil.createNonce()
        val timestamp = DateUtil.getSecondsFromEpoch()

        val xAuthSignature = prefKeys.signData?.let { signData ->
            cryptoController.createSessionSignature(
                requestMethod = request.method,
                requestUrl = request.url,
                requestBody = request.body.toJsonString(),
                requestHeaders = request.headers,
                nonce = nonce,
                timestamp = timestamp,
                signData = signData
            )
        }.orEmpty()

        logger.d("RequestController") { "(createSessionSignature) xAuthSignature: $xAuthSignature" }

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
}
