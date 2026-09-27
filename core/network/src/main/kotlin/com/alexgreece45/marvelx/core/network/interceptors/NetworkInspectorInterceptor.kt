package com.alexgreece45.marvelx.core.network.interceptors

import android.content.Context
import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.constants.ConstantsSecurity
import com.alexgreece45.marvelx.core.network.storage.NetworkInspectorSettings
import com.alexgreece45.marvelx.model.model.SensitiveNetworkDataMode
import com.chuckerteam.chucker.api.BodyDecoder
import com.chuckerteam.chucker.api.ChuckerInterceptor
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.Response
import okio.ByteString
import org.koin.core.annotation.Single

/**
 * Captures requests in the network inspector (Chucker; a no-op in release builds), treating
 * sensitive data according to the [SensitiveNetworkDataMode] chosen in the app's settings. The mode
 * is read for every request, so a change applies immediately.
 */
@Single
internal class NetworkInspectorInterceptor(
    context: Context,
    private val networkInspectorSettings: NetworkInspectorSettings,
    private val configOauthLogic: ConfigOauthLogic,
) : Interceptor {

    /**
     * Redacts credential headers, and masks sensitive body values before Chucker stores them
     * (Chucker persists what the first matching [BodyDecoder] returns, not the raw body).
     */
    private val maskingChucker = ChuckerInterceptor.Builder(context)
        .redactHeaders(
            ConstantsSecurity.HeaderName.Authorization.name,
            ConstantsSecurity.HeaderName.XAuthSignature.name
        )
        .addBodyDecoder(SensitiveDataMaskingBodyDecoder)
        .build()

    private val unmaskedChucker = ChuckerInterceptor.Builder(context).build()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        return when (networkInspectorSettings.getSensitiveNetworkDataMode()) {
            SensitiveNetworkDataMode.MASKED -> maskingChucker.intercept(chain)

            SensitiveNetworkDataMode.UNMASKED -> unmaskedChucker.intercept(chain)

            SensitiveNetworkDataMode.HIDDEN -> {
                if (request.url.encodedPath in tokenIssuingPaths) {
                    chain.proceed(request)
                } else {
                    maskingChucker.intercept(chain)
                }
            }
        }
    }

    /**
     * The paths whose responses carry the user's tokens: the token refresh, and the OTP validation
     * of the login.
     */
    private val tokenIssuingPaths: Set<String>
        get() = setOf(configOauthLogic.tokenPath, VALIDATE_OTP_PATH)

    private companion object {
        const val VALIDATE_OTP_PATH = "/api/private/marvelx/user/validate_otp"
    }
}

/**
 * Decodes textual bodies (JSON, form-encoded, text) with their sensitive values masked. Returns
 * `null` for anything else, which leaves the body to Chucker's built-in decoders.
 */
private object SensitiveDataMaskingBodyDecoder : BodyDecoder {

    override fun decodeRequest(request: Request, body: ByteString): String? =
        body.decodeMaskedTextOrNull(request.body?.contentType())

    override fun decodeResponse(response: Response, body: ByteString): String? =
        body.decodeMaskedTextOrNull(response.header(CONTENT_TYPE_HEADER)?.toMediaTypeOrNull())

    private fun ByteString.decodeMaskedTextOrNull(mediaType: MediaType?): String? {
        if (mediaType == null || !mediaType.isText()) return null
        return string(mediaType.charset() ?: Charsets.UTF_8).redactSensitiveData()
    }

    private fun MediaType.isText(): Boolean =
        type == "text" ||
            subtype == "json" || subtype.endsWith("+json") ||
            subtype == "x-www-form-urlencoded" ||
            subtype == "xml" || subtype.endsWith("+xml")

    private const val CONTENT_TYPE_HEADER = "Content-Type"
}
