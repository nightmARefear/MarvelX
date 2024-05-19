package com.alexgreece45.marvelx.di.network.interceptors

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
import com.alexgreece45.marvelx.util.extensions.toMd5
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class BaseUrlChangerAndAuthenticationInterceptor(
    private val configLogic: ConfigLogic,
    private val authController: AuthController
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        originalRequest.url.toUri().path.let { path ->
            when {
                // Marvel API.
                path.startsWith("/v1/public/") -> {
                    return applyMarvelApiTransformations(chain)
                }

                // MarvelX Backend API.
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
        val timestamp = (System.currentTimeMillis() / 1000).toString()
        val hash =
            "$timestamp${BuildConfig.API_PRIVATE_KEY}${BuildConfig.API_PUBLIC_KEY}".toMd5()

        val finalUrl = chain
            .request()
            .url
            .newBuilder()
            .addQueryParameter("ts", timestamp)
            .addQueryParameter("apikey", BuildConfig.API_PUBLIC_KEY)
            .addQueryParameter("hash", hash)
            .build()
        return chain.proceed(
            chain
                .request()
                .newBuilder()
                .url(finalUrl)
                .build()
        )
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

        // Create modified URL based on the config.
        val modifiedUrl =
            originalRequest.url.newBuilder()
                .scheme(configLogic.scheme)
                .host(configLogic.host)
                .port(configLogic.port)
                .build()

        // Create modified request based on the modifiedUrl and the config.
        val modifiedRequest = Request.Builder()
            .url(modifiedUrl)
            .addHeader(
                name = ConstantsSecurity.HeaderNames.UserAgent.name,
                value = ConstantsSecurity.HeaderValues.APP_USER_AGENT
            )
            .addHeader(
                name = ConstantsSecurity.HeaderNames.Authorization.name,
                value = if (path.endsWith("retrieve_timestamp")) authController.getRefreshToken()
                    .orEmpty() else authController.getAccessToken().orEmpty()
            )
            .method(originalRequest.method, originalRequest.body)

        return chain.proceed(modifiedRequest.build())
    }
}