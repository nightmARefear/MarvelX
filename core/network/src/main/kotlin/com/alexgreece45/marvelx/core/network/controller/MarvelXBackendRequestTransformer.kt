package com.alexgreece45.marvelx.core.network.controller

import com.alexgreece45.marvelx.core.common.constants.ConstantsSecurity
import com.alexgreece45.marvelx.core.network.requestSigner.RequestSigner
import com.alexgreece45.marvelx.core.network.storage.TokenStorage
import okhttp3.Request
import org.koin.core.annotation.Single

/**
 * Modifies all the requests that are intended for the MarvelX backend.
 */
internal interface MarvelXBackendRequestTransformer {

    /**
     * Applies the necessary transformations to produce the final request.
     *
     * @param originalRequest   The original request.
     * @param path              The URI path.
     */
    fun applyMarvelXBackendTransformations(
        originalRequest: Request,
        path: String
    ): Request
}

@Single
internal class MarvelXBackendRequestTransformerImpl(
    private val tokenStorage: TokenStorage,
    private val requestSigner: RequestSigner
) : MarvelXBackendRequestTransformer {

    override fun applyMarvelXBackendTransformations(
        originalRequest: Request,
        path: String
    ): Request {
        // Construct final request.
        val finalRequest = Request.Builder()
            .url(originalRequest.url)
            .addHeader(
                name = ConstantsSecurity.HeaderName.UserAgent.name,
                value = ConstantsSecurity.HeaderValue.APP_USER_AGENT
            )
            .method(originalRequest.method, originalRequest.body)

        // Add authorization headers only if the path of this request isn't in the excluded paths list.
        if (excludedRequestPathsFromAuthorization.all { it != path }) {
            finalRequest.addHeader(
                name = ConstantsSecurity.HeaderName.Authorization.name,
                value = tokenStorage.getAccessTokenAndType()
            )
            requestSigner.addAuthenticationHeaders(finalRequest)
        }

        return finalRequest.build()
    }

    /**
     * Contains a [List] of all the excluded request paths that don't require authorization of any
     * kind -- i.e., passing in the header the access / refresh token and generating a session
     * signature.
     *
     * The requests included in this [List], won't have any authorization headers.
     */
    private val excludedRequestPathsFromAuthorization: List<String>
        get() = listOf(
            "/api/private/marvelx/user/signup",
            "/api/private/marvelx/user/login",
            "/api/private/marvelx/user/post_login",
            "/api/private/marvelx/user/logout",
            "/api/private/marvelx/user/validate_otp",
            "/api/private/marvelx/user/resend_otp",
            "/api/private/marvelx/user/forgot_password/send_otp",
            "/api/private/marvelx/user/forgot_password/validate_otp"
        )
}