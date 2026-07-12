package com.alexgreece45.marvelx.core.network.interceptors

import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.common.util.DateUtil
import com.alexgreece45.marvelx.core.network.controller.MarvelXBackendRequestTransformer
import com.alexgreece45.marvelx.core.network.di.model.ApiInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.koin.core.annotation.Single

/**
 * Interceptor class used by [OkHttpClient] to intercept network requests. All required headers are
 * added here.
 */
@Single
@ApiInterceptor
internal class ApiInterceptorImpl(
//    private val resourceProvider: ResourceProvider,
    private val configLogic: ConfigLogic,
    private val marvelXBackendRequestTransformer: MarvelXBackendRequestTransformer
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
//        if (resourceProvider.getContext().hasInternetConnectivity().not()) {
//            throw IOException(resourceProvider.getNoInternetConnectionMessage())
//        }

        val originalRequest = chain.request()
        originalRequest.url.toUri().path.let { path ->
            when {
                // Marvel API.
                path.startsWith(configLogic.marvelApiConfig.endpointPrefix) -> {
                    return applyMarvelApiTransformations(chain)
                }

                // MarvelX Backend.
                path.startsWith(configLogic.backendEndpointPrefix) -> {
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
        // TODO: Retrieve keys from backend.
        val timestamp = DateUtil.getEpochSeconds().toString()
//        val hash =
//            "$timestamp${BuildConfig.API_PRIVATE_KEY}${BuildConfig.API_PUBLIC_KEY}".toMd5()

        val hash = "[ERROR] RETRIEVE KEYS FROM BACKEND."

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
            .addQueryParameter("apikey", "[PUBLIC_API_KEY]")
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
        val finalRequest = marvelXBackendRequestTransformer.applyMarvelXBackendTransformations(
            originalRequest = chain.request(),
            path = path
        )
        return chain.proceed(finalRequest)
    }
}