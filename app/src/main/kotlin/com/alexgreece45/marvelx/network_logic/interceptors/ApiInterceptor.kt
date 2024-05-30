package com.alexgreece45.marvelx.network_logic.interceptors

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogic
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.RequestController
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
import com.alexgreece45.marvelx.util.extensions.hasInternetConnectivity
import com.alexgreece45.marvelx.util.extensions.toMd5
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

/**
 * Interceptor class used by [OkHttpClient] to intercept network requests. All required headers are
 * added here.
 */
class ApiInterceptor(
    private val configLogic: ConfigLogic,
    private val configSecurityLogic: ConfigSecurityLogic,
    private val authController: AuthController,
    private val resourceProvider: ResourceProvider,
    private val requestController: RequestController
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (resourceProvider.getContext().hasInternetConnectivity().not()) {
            throw IOException(resourceProvider.getNoInternetConnectionMessage())
        }

        val originalRequest = chain.request()
        originalRequest.url.toUri().path.let { path ->
            when {
                // Marvel API.
                path.startsWith(configLogic.marvelApiConfig.endpointPrefix) -> {
                    return applyMarvelApiTransformations(chain)
                }

                // MarvelX Backend.
                path.startsWith(BuildConfig.MARVELX_BACKEND_ENDPOINT_PREFIX) -> {
                    return applyMarvelXBackendTransformations(
                        path = path,
                        chain = chain
                    )
                }

                // Other API.
                else -> {
                    return chain.proceed(originalRequest)
                }
            }
        }
    }

    /**
     * Applies the necessary transformations for the Marvel API.
     */
    private fun applyMarvelApiTransformations(chain: Interceptor.Chain): Response {
        val timestamp = DateUtil.getSecondsFromEpoch().toString()
        val hash =
            "$timestamp${BuildConfig.API_PRIVATE_KEY}${BuildConfig.API_PUBLIC_KEY}".toMd5()

        val originalRequest = chain.request()

        // Add new URL, and retrieve method and body from the original request and add them.
        val finalRequest = Request.Builder()
            .url(configLogic.marvelApiConfig.url)
            .method(originalRequest.method, originalRequest.body)
            .build()

        val finalUrlBuilder = finalRequest.url.newBuilder()

        // Add path segments from original request.
        originalRequest.url.pathSegments.forEach { pathSegment ->
            finalUrlBuilder.addPathSegment(pathSegment)
        }

        // Add query parameters for authentication & authorization.
        finalUrlBuilder
            .addQueryParameter("ts", timestamp)
            .addQueryParameter("apikey", BuildConfig.API_PUBLIC_KEY)
            .addQueryParameter("hash", hash)

        // Rebuild request that includes "finalUrl".
        val actualRequest = finalRequest.newBuilder().url(finalUrlBuilder.build()).build()

        // Proceed with the new request.
        return chain.proceed(actualRequest)
    }

    /**
     * Applies the necessary transformations for the Marvel backend.
     */
    private fun applyMarvelXBackendTransformations(
        path: String,
        chain: Interceptor.Chain
    ): Response {
        // Get request from chain.
        val originalRequest = chain.request()

        // Create modified request based on the modifiedUrl and the config.
        val modifiedRequest = Request.Builder()
            .url(originalRequest.url)
            .addHeader(
                name = ConstantsSecurity.HeaderName.UserAgent.name,
                value = ConstantsSecurity.HeaderValue.APP_USER_AGENT
            )
            .method(originalRequest.method, originalRequest.body)

        // Add authorization headers only if the path of this request isn't in the excluded paths list.
        if (configSecurityLogic.excludedRequestPathsFromAuthorization.all { it != path }) {
            modifiedRequest.addHeader(
                name = ConstantsSecurity.HeaderName.Authorization.name,
                value = authController.getAccessToken()
            )

            requestController.addAuthenticationHeaders(modifiedRequest)
        }

        return chain.proceed(modifiedRequest.build())
    }
}