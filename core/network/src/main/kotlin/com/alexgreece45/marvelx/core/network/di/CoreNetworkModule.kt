package com.alexgreece45.marvelx.core.network.di

import android.content.Context
import com.alexgreece45.marvelx.core.common.config.ConfigLogic
import com.alexgreece45.marvelx.core.common.config.SecurityConfigLogic
import com.alexgreece45.marvelx.core.common.config.WebsocketConfigLogic
import com.alexgreece45.marvelx.core.network.clients.ApiClient
import com.alexgreece45.marvelx.core.network.clients.RefreshTokenClient
import com.alexgreece45.marvelx.core.network.di.model.ApiAuthenticator
import com.alexgreece45.marvelx.core.network.di.model.ApiInterceptor
import com.alexgreece45.marvelx.core.network.di.model.BackendOkHttpClient
import com.alexgreece45.marvelx.core.network.di.model.BackendRetrofit
import com.alexgreece45.marvelx.core.network.di.model.RefreshAuthTokenOkHttpClient
import com.alexgreece45.marvelx.core.network.di.model.RefreshTokenAuthenticator
import com.alexgreece45.marvelx.core.network.di.model.RefreshTokenInterceptor
import com.alexgreece45.marvelx.core.network.di.model.RefreshTokenRetrofit
import com.alexgreece45.marvelx.core.network.di.model.WebsocketOkHttpClient
import com.alexgreece45.marvelx.core.network.di.model.WebsocketInterceptor
import com.alexgreece45.marvelx.core.network.model.api.character.comic.ResponseComicPriceType
import com.alexgreece45.marvelx.core.network.model.api.verification_step.ResponseVerificationStep
import com.alexgreece45.marvelx.core.network.model.websocket.ResponseWebsocketRequestType
import com.alexgreece45.marvelx.core.network.services.ApiService
import com.alexgreece45.marvelx.core.network.services.ListingService
import com.alexgreece45.marvelx.core.network.services.OrderService
import com.alexgreece45.marvelx.core.network.services.RefreshTokenService
import com.alexgreece45.marvelx.core.network.typeAdapters.InstantTypeAdapter
import com.alexgreece45.marvelx.core.network.typeAdapters.OffsetDateTimeAdapter
import com.alexgreece45.marvelx.core.network.typeAdapters.ResponseComicPriceTypeAdapter
import com.alexgreece45.marvelx.core.network.typeAdapters.ResponseVerificationStepAdapter
import com.alexgreece45.marvelx.core.network.typeAdapters.ResponseWebsocketRequestTypeAdapter
import com.alexgreece45.marvelx.core.network.typeAdapters.UUIDAdapter
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.CertificatePinner
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@Module
@ComponentScan("com.alexgreece45.marvelx.core.network")
//@Configuration
class CoreNetworkModule

/**
 * Provides a [Gson] instance with the type adapters for the following types:
 *  - [OffsetDateTime]
 *  - [ResponseComicPriceType]
 *  - [ResponseVerificationStep]
 *  - [ResponseWebsocketRequestType]
 *  - [UUID]
 *  - [Instant]
 */
@Single
internal fun provideGson(): Gson = GsonBuilder()
    .registerTypeAdapter(
        OffsetDateTime::class.java,
        OffsetDateTimeAdapter()
    )
    .registerTypeAdapter(
        ResponseComicPriceType::class.java,
        ResponseComicPriceTypeAdapter()
    )
    .registerTypeAdapter(
        ResponseVerificationStep::class.java,
        ResponseVerificationStepAdapter()
    )
    .registerTypeAdapter(
        ResponseWebsocketRequestType::class.java,
        ResponseWebsocketRequestTypeAdapter()
    )
    .registerTypeAdapter(
        UUID::class.java,
        UUIDAdapter()
    )
    .registerTypeAdapter(
        Instant::class.java,
        InstantTypeAdapter()
    )
    .create()

@Single
internal fun provideJson(): Json = Json { ignoreUnknownKeys = true }

/**
 * Provides the [OkHttpClient] for the [ApiClient].
 */
@Single
@BackendOkHttpClient
internal fun provideOkHttpClient(
    context: Context,
    @ApiAuthenticator apiAuthenticator: Authenticator,
    @ApiInterceptor apiInterceptor: Interceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
    securityConfigLogic: SecurityConfigLogic,
    configLogic: ConfigLogic
): OkHttpClient {
    val okHttpClient = OkHttpClient.Builder()

    // Set timeouts.
    okHttpClient.connectTimeout(configLogic.backendTimeoutData.connectTimeout, TimeUnit.SECONDS)
    okHttpClient.readTimeout(configLogic.backendTimeoutData.readTimeout, TimeUnit.SECONDS)

    // Adds certificate pins.
    val certificatePinner = CertificatePinner.Builder()
        .add(
            pattern = configLogic.backendDomain,
            pins = arrayOf("sha256/${configLogic.backendDomainPin}")
        )
        .build()
    okHttpClient.certificatePinner(certificatePinner)

    // Add authenticator.
    okHttpClient.authenticator(apiAuthenticator)

    // Adds required interceptors.
    okHttpClient.addInterceptor(apiInterceptor)
    if (securityConfigLogic.useNetworkLogger) {
        okHttpClient.addInterceptor(httpLoggingInterceptor)
    }

    okHttpClient.addInterceptor(ChuckerInterceptor(context))

    // Disables response caching for security reasons.
    okHttpClient.cache(null)

    return okHttpClient.build()
}

/**
 * Provides the [OkHttpClient] for the [RefreshTokenClient].
 */
@Single
@RefreshAuthTokenOkHttpClient
internal fun provideOkHttpClientForRefreshTokenClient(
    context: Context,
    @RefreshTokenAuthenticator refreshTokenAuthenticator: Authenticator,
    @RefreshTokenInterceptor refreshTokenInterceptor: Interceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
    securityConfigLogic: SecurityConfigLogic,
    configLogic: ConfigLogic
): OkHttpClient {
    val okHttpClient = OkHttpClient.Builder()

    // Set timeouts.
    okHttpClient.connectTimeout(configLogic.backendTimeoutData.connectTimeout, TimeUnit.SECONDS)
    okHttpClient.readTimeout(configLogic.backendTimeoutData.readTimeout, TimeUnit.SECONDS)

    // Adds certificate pins.
    val certificatePinner = CertificatePinner.Builder()
        .add(
            pattern = configLogic.backendDomain,
            pins = arrayOf("sha256/${configLogic.backendDomainPin}")
        )
        .build()
    okHttpClient.certificatePinner(certificatePinner)

    // Add authenticator.
    okHttpClient.authenticator(refreshTokenAuthenticator)

    // Adds required interceptors.
    okHttpClient.addInterceptor(refreshTokenInterceptor)
    if (securityConfigLogic.useNetworkLogger) {
        okHttpClient.addInterceptor(httpLoggingInterceptor)
    }

    okHttpClient.addInterceptor(ChuckerInterceptor(context))

    // Disables response caching for security reasons.
    okHttpClient.cache(null)

    return okHttpClient.build()
}

/**
 * Provides the [OkHttpClient] for the websocket client.
 */
@Single
@WebsocketOkHttpClient
internal fun provideOkHttpClientForWebsocketClient(
    context: Context,
    httpLoggingInterceptor: HttpLoggingInterceptor,
    @WebsocketInterceptor websocketInterceptor: Interceptor,
    securityConfigLogic: SecurityConfigLogic,
    websocketConfigLogic: WebsocketConfigLogic,
): OkHttpClient {
    val okHttpClient = OkHttpClient.Builder()

    // Set timeouts.
    okHttpClient.connectTimeout(websocketConfigLogic.timeoutData.connectTimeout, TimeUnit.SECONDS)
    okHttpClient.readTimeout(websocketConfigLogic.timeoutData.readTimeout, TimeUnit.SECONDS)

    // Add ping interval.
    okHttpClient.pingInterval(Duration.ofSeconds(30))

    // Add certificate pins.
    val certificatePinner = CertificatePinner.Builder()
        .add(
            pattern = websocketConfigLogic.domain,
            pins = arrayOf("sha256/${websocketConfigLogic.domainPin}")
        )
        .build()
    okHttpClient.certificatePinner(certificatePinner)

    // Add interceptors.
    if (securityConfigLogic.useNetworkLogger) {
        okHttpClient.addInterceptor(httpLoggingInterceptor)
    }
    okHttpClient.addInterceptor(websocketInterceptor)

    okHttpClient.addInterceptor(ChuckerInterceptor(context))

    return okHttpClient.build()
}

@Single
internal fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
    GsonConverterFactory.create(gson)

@Single
@BackendRetrofit
internal fun provideRetrofit(
    @BackendOkHttpClient okHttpClient: OkHttpClient,
    converterFactory: GsonConverterFactory,
    configLogic: ConfigLogic
): Retrofit = Retrofit.Builder()
    .baseUrl(configLogic.serverUrl)
    .addConverterFactory(converterFactory)
    .client(okHttpClient)
    .build()

@Single
@RefreshTokenRetrofit
internal fun provideRetrofitForRefreshTokenService(
    @RefreshAuthTokenOkHttpClient okHttpClient: OkHttpClient,
    converterFactory: GsonConverterFactory,
    configLogic: ConfigLogic
): Retrofit = Retrofit.Builder()
    .baseUrl(configLogic.serverUrl)
    .addConverterFactory(converterFactory)
    .client(okHttpClient)
    .build()

@Single
internal fun provideApiService(@BackendRetrofit retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)

@Single
internal fun provideRefreshTokenService(@RefreshTokenRetrofit retrofit: Retrofit): RefreshTokenService =
    retrofit.create(RefreshTokenService::class.java)

@Single
internal fun provideListingService(@BackendRetrofit retrofit: Retrofit): ListingService =
    retrofit.create(ListingService::class.java)

@Single
internal fun provideOrderService(@BackendRetrofit retrofit: Retrofit): OrderService =
    retrofit.create(OrderService::class.java)
