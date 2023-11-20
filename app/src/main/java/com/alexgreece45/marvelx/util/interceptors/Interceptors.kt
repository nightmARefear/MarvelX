package com.alexgreece45.marvelx.util.interceptors

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.util.APP_USER_AGENT
import com.alexgreece45.marvelx.util.HeaderConstants
import com.alexgreece45.marvelx.util.extensions.toMd5
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BaseUrlChangerAndAuthenticationInterceptor @Inject constructor(
    private val authController: AuthController,
    private val configLogic: ConfigLogic
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
                        chain = chain,
                        authController = authController,
                        configLogic = configLogic
                    )
                }

                // Other API.
                else -> {
                    return chain.proceed(originalRequest)
                }
            }
        }
    }
}

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

private fun applyMarvelXBackendTransformations(
    path: String,
    chain: Interceptor.Chain,
    authController: AuthController,
    configLogic: ConfigLogic
): Response {
    // Get request from chain.
    val originalRequest = chain.request()

    // Get scheme from MarvelX Backend's baseUrl.
    val scheme = configLogic.scheme

    // Get host from config.
    val host = configLogic.host

    // Get port from config.
    val port = configLogic.port

    // Create modified URL based on the config.
    val modifiedUrl =
        originalRequest.url.newBuilder()
            .scheme(scheme)
            .host(host)
            .port(port)
            .build()

    // Create modified request based on the modifiedUrl and the config.
    var modifiedRequest = Request.Builder()
        .url(modifiedUrl)
        .addHeader(HeaderConstants.UserAgent.name, APP_USER_AGENT)
        .method(originalRequest.method, originalRequest.body)

    // Add headers where necessary.
    when {
        path.contains("login_with_token") || path.contains("profile_details") -> {
            modifiedRequest = modifiedRequest
                .addHeader(HeaderConstants.Authorization.name, "${authController.getAccessToken()}")
        }

        else -> {}
    }
    return chain.proceed(modifiedRequest.build())
}