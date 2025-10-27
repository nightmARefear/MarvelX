package com.alexgreece45.marvelx.controllers

import com.alexgreece45.marvelx.configuration.ConfigSecurityLogic
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
import okhttp3.Request

/**
 * This controller modifies all the requests that are intended for the MarvelX backend.
 */
interface MarvelXBackendRequestController {

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

class MarvelXBackendRequestControllerImpl(
    private val configSecurityLogic: ConfigSecurityLogic,
    private val authController: AuthController,
    private val requestController: RequestController
) : MarvelXBackendRequestController {

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
        if (configSecurityLogic.excludedRequestPathsFromAuthorization.all { it != path }) {
            finalRequest.addHeader(
                name = ConstantsSecurity.HeaderName.Authorization.name,
                value = authController.getAccessToken()
            )
            requestController.addAuthenticationHeaders(finalRequest)
        }

        return finalRequest.build()
    }
}
