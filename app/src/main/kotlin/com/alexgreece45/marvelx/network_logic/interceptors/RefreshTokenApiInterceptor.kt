package com.alexgreece45.marvelx.network_logic.interceptors

import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.configuration.ConfigOauth
import com.alexgreece45.marvelx.util.constants.ConstantsSecurity
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * The interceptor for the `Refresh Token Client`.
 */
class RefreshTokenInterceptor(
    private val configOauth: ConfigOauth
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.url.toUri().path != configOauth.tokenPath) {
            return chain.proceed(originalRequest)
        }

        val finalRequest = Request.Builder()
            .url(originalRequest.url)
            .addHeader(
                name = ConstantsSecurity.HeaderName.UserAgent.name,
                value = ConstantsSecurity.HeaderValue.APP_USER_AGENT
            )
            .method(originalRequest.method, originalRequest.body)

        return chain.proceed(finalRequest.build())
    }
}
