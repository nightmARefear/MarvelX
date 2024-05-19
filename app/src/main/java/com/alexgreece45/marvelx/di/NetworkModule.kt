package com.alexgreece45.marvelx.di

import android.content.Context
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.configuration.AppBuildType
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.controllers.WebsocketControllerImpl
import com.alexgreece45.marvelx.data.domains.comic.ComicPriceType
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.util.gson.ComicPriceTypeAdapter
import com.alexgreece45.marvelx.util.gson.OffsetDateTimeAdapter
import com.alexgreece45.marvelx.util.gson.VerificationStepAdapter
import com.alexgreece45.marvelx.util.interceptors.BaseUrlChangerAndAuthenticationInterceptor
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.OffsetDateTime

object NetworkModule {

    private fun provideRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(converterFactory)
        .client(okHttpClient)
        .build()

    private fun provideHttpLoggingInterceptor(
        appLoggerController: AppLoggerController,
        configLogic: ConfigLogic
    ): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            appLoggerController.d { message }
        }.apply {
            level = when (configLogic.appBuildType) {
                AppBuildType.RELEASE -> HttpLoggingInterceptor.Level.NONE
                else -> HttpLoggingInterceptor.Level.BODY
            }
        }
    }

    private fun provideOkHttpClient(
        @ApplicationContext context: Context,
        interceptor: BaseUrlChangerAndAuthenticationInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        configLogic: ConfigLogic
    ): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()

        // Add certificate pins.
        val certificatePinner = CertificatePinner.Builder()
            .add(
                pattern = BuildConfig.MARVELX_BACKEND_DOMAIN,
                pins = arrayOf("sha256/${BuildConfig.MARVELX_BACKEND_DOMAIN_PIN}")
            )
            .build()
        okHttpClient.certificatePinner(certificatePinner)

        // Add required interceptors.
        okHttpClient.addInterceptor(interceptor)
        okHttpClient.addInterceptor(httpLoggingInterceptor)

        // Add "ChuckerInterceptor" only in non-release builds (debug, mock).
        if (configLogic.appBuildType != AppBuildType.RELEASE) {
            okHttpClient.addInterceptor(ChuckerInterceptor(context))
        }

        return okHttpClient.build()
    }

    private fun provideOkHttpClientForWebsocketClient(
        @ApplicationContext context: Context,
        httpLoggingInterceptor: HttpLoggingInterceptor,
        configLogic: ConfigLogic
    ): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()

        // Add certificate pins.
        val certificatePinner = CertificatePinner.Builder()
            .add(
                pattern = BuildConfig.MARVELX_BACKEND_WEBSOCKET_DOMAIN,
                pins = arrayOf("sha256/${BuildConfig.MARVELX_BACKEND_WEBSOCKET_DOMAIN_PIN}")
            )
            .build()
        okHttpClient.certificatePinner(certificatePinner)

        // Add interceptors.
        okHttpClient.addInterceptor(httpLoggingInterceptor)

        // Add "ChuckerInterceptor" only in non-release builds (debug, mock).
        if (configLogic.appBuildType != AppBuildType.RELEASE) {
            okHttpClient.addInterceptor(ChuckerInterceptor(context))
        }

        return okHttpClient.build()
    }

    private fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    private fun provideGson(): Gson = GsonBuilder()
        .registerTypeAdapter(
            OffsetDateTime::class.java,
            OffsetDateTimeAdapter()
        )
        .registerTypeAdapter(
            ComicPriceType::class.java,
            ComicPriceTypeAdapter()
        )
        .registerTypeAdapter(
            VerificationStep::class.java,
            VerificationStepAdapter()
        )
        .create()

    val koinModule = module {
        single<Gson> { provideGson() }
        single<GsonConverterFactory> { provideGsonConverterFactory(get()) }
        single<HttpLoggingInterceptor> { provideHttpLoggingInterceptor(get(), get()) }
        single<OkHttpClient> { provideOkHttpClient(get(), get(), get(), get()) }
        single<Retrofit> { provideRetrofit(get(), get()) }

        // Websocket client.
        single<WebsocketController> {
            WebsocketControllerImpl(
                get(),
                provideOkHttpClientForWebsocketClient(get(), get(), get()),
                get(),
                get()
            )
        }
    }
}