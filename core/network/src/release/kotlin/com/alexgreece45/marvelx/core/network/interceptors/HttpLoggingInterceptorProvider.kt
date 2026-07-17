package com.alexgreece45.marvelx.core.network.interceptors

import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.Single

@Single
internal fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor {}.apply { level = HttpLoggingInterceptor.Level.NONE }
}