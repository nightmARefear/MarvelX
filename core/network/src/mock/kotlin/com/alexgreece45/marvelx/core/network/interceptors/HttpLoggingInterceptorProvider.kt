package com.alexgreece45.marvelx.core.network.interceptors

import com.alexgreece45.marvelx.core.common.constants.ConstantsSecurity
import com.alexgreece45.marvelx.core.common.logger.AppLogger
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.Single

@Single
internal fun provideHttpLoggingInterceptor(appLogger: AppLogger): HttpLoggingInterceptor {
    return HttpLoggingInterceptor { message ->
        // Bodies are logged, so OAuth/PKCE secrets and tokens are masked (e.g., the `/token`
        // exchange's `code_verifier` and `code`, and the tokens in its response).
        appLogger.d { message.redactSensitiveData() }
    }.apply {
        level = HttpLoggingInterceptor.Level.BODY
        redactHeader(ConstantsSecurity.HeaderName.Authorization.name)
        redactHeader(ConstantsSecurity.HeaderName.XAuthSignature.name)
    }
}
