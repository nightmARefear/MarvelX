package com.alexgreece45.marvelx.util.interceptors

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.util.extensions.toMd5
import okhttp3.Interceptor
import okhttp3.Response
import java.time.Instant

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
}
