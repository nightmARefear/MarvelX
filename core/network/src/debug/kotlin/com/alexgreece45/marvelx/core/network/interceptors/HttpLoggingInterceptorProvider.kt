package com.alexgreece45.marvelx.core.network.interceptors

import com.alexgreece45.marvelx.core.common.logger.AppLogger
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.Single

@Single
internal fun provideHttpLoggingInterceptor(appLogger: AppLogger): HttpLoggingInterceptor {
    return HttpLoggingInterceptor { message ->
        appLogger.d { message }
    }.apply { level = HttpLoggingInterceptor.Level.BODY }
}