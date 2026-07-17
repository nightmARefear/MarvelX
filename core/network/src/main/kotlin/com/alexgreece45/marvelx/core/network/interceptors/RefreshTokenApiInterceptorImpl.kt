package com.alexgreece45.marvelx.core.network.interceptors

import com.alexgreece45.marvelx.core.common.config.ConfigOauthLogic
import com.alexgreece45.marvelx.core.common.constants.ConstantsSecurity
import com.alexgreece45.marvelx.core.network.di.model.RefreshTokenInterceptor
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import org.koin.core.annotation.Single

/**
 * The interceptor for the `Refresh Token Client`.
 */
@Single
@RefreshTokenInterceptor
internal class RefreshTokenInterceptorImpl(
    private val configOauthLogic: ConfigOauthLogic
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.url.toUri().path != configOauthLogic.tokenPath) {
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