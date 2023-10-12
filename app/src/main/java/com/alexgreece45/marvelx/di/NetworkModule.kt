package com.alexgreece45.marvelx.di

import android.content.Context
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.data.api.ApiService
import com.alexgreece45.marvelx.data.domains.ComicPriceType
import com.alexgreece45.marvelx.data.domains.user.VerificationStep
import com.alexgreece45.marvelx.util.gson.ComicPriceTypeAdapter
import com.alexgreece45.marvelx.util.gson.OffsetDateTimeAdapter
import com.alexgreece45.marvelx.util.gson.VerificationStepAdapter
import com.alexgreece45.marvelx.util.interceptors.Interceptors
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import logcat.logcat
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.OffsetDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideApi(
        @ApplicationContext context: Context
    ): ApiService = provideRetrofit(
        BuildConfig.BASE_URL,
        OkHttpClient.Builder()
            .addInterceptor(
                Interceptors.AuthenticationTokenInterceptor
            )
            .addInterceptor(
                Interceptors.MarvelXBackEndChangeBaseUrlInterceptor
            )
            .addInterceptor(
                HttpLoggingInterceptor(
                    object : HttpLoggingInterceptor.Logger {
                        override fun log(message: String) {
                            logcat { message }
                        }
                    }
                ).apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .addInterceptor(ChuckerInterceptor(context))
            .build(),
        GsonConverterFactory.create(
            provideGson()
        )
    ).create(
        ApiService::class.java
    )

    @Provides
    @Singleton
    fun provideRetrofit(
        baseUrl: String,
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(converterFactory)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideGson(): Gson = GsonBuilder()
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
}