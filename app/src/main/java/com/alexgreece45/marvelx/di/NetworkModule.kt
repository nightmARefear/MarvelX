package com.alexgreece45.marvelx.di

import android.content.Context
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.data.domains.comic.ComicPriceType
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
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        interceptor: Interceptors.MarvelXBackendChangeBaseUrlInterceptor
    ): OkHttpClient {
        val okHttpClient = OkHttpClient.Builder()

        // Add required interceptors.
        okHttpClient.addInterceptor(Interceptors.AuthenticationTokenInterceptor())
        okHttpClient.addInterceptor(interceptor)
        okHttpClient.addInterceptor(
            HttpLoggingInterceptor(
                object : HttpLoggingInterceptor.Logger {
                    override fun log(message: String) {
                        logcat { message }
                    }
                }
            ).apply {
                level = when (BuildConfig.LOG_MODE && BuildConfig.DEBUG) {
                    true -> HttpLoggingInterceptor.Level.BODY
                    false -> HttpLoggingInterceptor.Level.NONE
                }
            }
        )

        // Add "ChuckerInterceptor" only in debuggable builds (debug, mock).
        if (BuildConfig.LOG_MODE && BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(ChuckerInterceptor(context))
        }

        return okHttpClient.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        converterFactory: GsonConverterFactory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(converterFactory)
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

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