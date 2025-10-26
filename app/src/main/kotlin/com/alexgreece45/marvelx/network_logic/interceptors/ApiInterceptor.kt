package com.alexgreece45.marvelx.network_logic.interceptors

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.controllers.MarvelXBackendRequestController
import com.alexgreece45.marvelx.util.DateUtil
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.extensions.hasInternetConnectivity
import com.alexgreece45.marvelx.util.extensions.toMd5
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException
import org.koin.core.annotation.Single

/**
 * Interceptor class used by [OkHttpClient] to intercept network requests. All required headers are
 * added here.
 */
@Single
class ApiInterceptor(
    private val resourceProvider: ResourceProvider,
    private val configLogic: ConfigLogic,
    private val marvelXBackendRequestController: MarvelXBackendRequestController
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
        val finalRequest = marvelXBackendRequestController.applyMarvelXBackendTransformations(
            originalRequest = chain.request(),
            path = path
        )
        return chain.proceed(finalRequest)
    }
}
