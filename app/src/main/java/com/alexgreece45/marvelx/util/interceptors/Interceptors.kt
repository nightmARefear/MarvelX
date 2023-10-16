package com.alexgreece45.marvelx.util.interceptors

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.domain.controllers.PreferencesKeys
import com.alexgreece45.marvelx.util.APP_USER_AGENT
import com.alexgreece45.marvelx.util.HeaderConstants
import com.alexgreece45.marvelx.util.extensions.toMd5
import logcat.logcat
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

sealed class Interceptors : Interceptor {
    class AuthenticationTokenInterceptor : Interceptors() {
        override fun intercept(chain: Interceptor.Chain): Response {
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
    }

    @Singleton
    class MarvelXBackendChangeBaseUrlInterceptor @Inject constructor(
        private val preferencesKeys: PreferencesKeys
    ) : Interceptors() {
        override fun intercept(chain: Interceptor.Chain): Response {
            val originalRequest = chain.request()
            if (originalRequest.url.toString().removePrefix(BuildConfig.BASE_URL)
                    .startsWith(BuildConfig.MARVELX_BACKEND_ENDPOINT_PREFIX)
            ) {
                // New base url that utilizes the MarvelX backend's base url, and does not contain any query parameters from
                // the original request.
                val newBaseUrl =
                    (BuildConfig.MARVELX_BACKEND_BASE_URL + originalRequest.url.toString()
                        .removePrefix(BuildConfig.BASE_URL).substringBefore("?")).toHttpUrlOrNull()
                newBaseUrl?.let { baseUrl ->
                    val modifiedUrl = baseUrl.newBuilder()
                        .scheme(newBaseUrl.scheme)
                        .host(baseUrl.host)
                        .port(baseUrl.port)
                        .build()

                    val token = preferencesKeys.getUserToken()

                    val modifiedRequest = Request.Builder()
                        .url(modifiedUrl)
                        .addHeader(HeaderConstants.UserAgent.name, APP_USER_AGENT)
                        // Authentication token.
                        .addHeader(HeaderConstants.Authorization.name, "Bearer $token")
                        .method(originalRequest.method, originalRequest.body)
                        .build()

                    logcat { "(MarvelXBackEndChangeBaseUrlInterceptor) modified: success" }
                    return chain.proceed(modifiedRequest)
                } ?: run {
                    logcat { "(MarvelXBackEndChangeBaseUrlInterceptor) modified: error" }
                }
            }
            // Runs either if an error occurred, or if the endpoint's prefix was not from the MarvelX-Backend.
            return chain.proceed(originalRequest)
        }
    }
}
