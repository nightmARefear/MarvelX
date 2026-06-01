package com.alexgreece45.marvelx.network.di

import com.alexgreece45.marvelx.controllers.AppLogger
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.Single

@Single
internal fun provideHttpLoggingInterceptor(
    appLogger: AppLogger
): HttpLoggingInterceptor =
    HttpLoggingInterceptor { message ->
        appLogger.d { message }
    }.apply {
        level = HttpLoggingInterceptor.Level.NONE
    }
