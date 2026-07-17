package com.alexgreece45.marvelx.core.network.interceptors

import com.alexgreece45.marvelx.core.network.di.model.WebsocketInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import org.koin.core.annotation.Single

/**
 * Interceptor class used by the [OkHttpClient] of the websocket client to intercept network
 * requests.
 */
@Single
@WebsocketInterceptor
internal class WebsocketInterceptorImpl : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
//        if (resourceProvider.getContext().hasInternetConnectivity().not()) {
//            throw IOException(resourceProvider.getNoInternetConnectionMessage())
//        }
        return chain.proceed(chain.request())
    }
}