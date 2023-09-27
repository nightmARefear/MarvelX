package com.alexgreece45.marvelx.util.interceptors

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.util.extensions.toMd5
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

sealed class Interceptors : Interceptor {
    object AuthenticationTokenInterceptor : Interceptors() {
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
                .addQueryParameter("hash", hash) // handle error
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

    object MarvelXBackEndChangeBaseUrlInterceptor : Interceptors() {
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
                    // old way
                    val modifiedUrl = baseUrl.newBuilder()
                        .scheme(newBaseUrl.scheme)
                        .host(baseUrl.host)
                        .port(baseUrl.port)
                        .build()

                    val modifiedRequest = Request.Builder()
                        .url(modifiedUrl)
                        .method(originalRequest.method, originalRequest.body)
                        .build()

                    println("(MarvelXBackEndChangeBaseUrlInterceptor) modified: success")
                    return chain.proceed(modifiedRequest)
                } ?: run {
                    println("(MarvelXBackEndChangeBaseUrlInterceptor) modified: error")
                }
            }
            // Runs either if an error occurred, or if the endpoint's prefix was not from the MarvelX-Backend.
            return chain.proceed(originalRequest)
        }
    }
}
