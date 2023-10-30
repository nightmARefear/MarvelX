package com.alexgreece45.marvelx.util.interceptors

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.domain.controllers.PreferencesKeys
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
    private val preferencesKeys: PreferencesKeys
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
                    return applyMarvelXBackendTransformations(path, chain, preferencesKeys)
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
    val timestampString = (System.currentTimeMillis() / 1000).toString()
    val hash =
        "$timestampString${BuildConfig.API_PRIVATE_KEY}${BuildConfig.API_PUBLIC_KEY}".toMd5()

    val finalUrl = chain
        .request()
        .url
        .newBuilder()
        .addQueryParameter("ts", timestampString)
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
    preferencesKeys: PreferencesKeys
): Response {
    val originalRequest = chain.request()
    // Get scheme from MarvelX Backend's baseUrl.
    val scheme =
        if (BuildConfig.MARVELX_BACKEND_BASE_URL.startsWith("https")) "https" else "http"

    // Get host from config.
    val host = BuildConfig.MARVELX_BACKEND_DOMAIN

    // Get port from config.
    val port = BuildConfig.MARVELX_BACKEND_BASE_URL.split(":").last().toInt()

    // Add headers where necessary.
    when {
        path.contains("login_with_token") || path.contains("profile_details") -> {
            val originalUrl = originalRequest.url
            val modifiedUrl =
                originalUrl.newBuilder()
                    .scheme(scheme)
                    .host(host)
                    .port(port)
                    .build()

            val token = preferencesKeys.getUserToken()

            val modifiedRequest = Request.Builder()
                .url(modifiedUrl)
                .addHeader(HeaderConstants.UserAgent.name, APP_USER_AGENT)
                // Authentication token.
                .addHeader(HeaderConstants.Authorization.name, "Bearer $token")
                .method(originalRequest.method, originalRequest.body)
                .build()

            return chain.proceed(modifiedRequest)
        }

        else -> {
            val originalUrl = originalRequest.url
            val modifiedUrl =
                originalUrl.newBuilder()
                    .scheme(scheme)
                    .host(host)
                    .port(port)
                    .build()

            val modifiedRequest = Request.Builder()
                .url(modifiedUrl)
                .addHeader(HeaderConstants.UserAgent.name, APP_USER_AGENT)
                .method(originalRequest.method, originalRequest.body)
                .build()

            return chain.proceed(modifiedRequest)
        }
    }
}

