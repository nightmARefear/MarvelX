package com.alexgreece45.marvelx.network_logic.di

import android.content.Context
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.configuration.AppBuildType
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogic
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.controllers.WebsocketControllerImpl
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketRequestType
import com.alexgreece45.marvelx.di.ApiClientImpl
import com.alexgreece45.marvelx.domain.model_api.comic.ComicPriceType
import com.alexgreece45.marvelx.domain.model_api.user.VerificationStep
import com.alexgreece45.marvelx.network_logic.api.ApiClient
import com.alexgreece45.marvelx.network_logic.api.ApiService
import com.alexgreece45.marvelx.network_logic.authenticators.ApiAuthenticator
import com.alexgreece45.marvelx.network_logic.authenticators.RefreshTokenAuthenticator
import com.alexgreece45.marvelx.network_logic.clients.RefreshTokenClient
import com.alexgreece45.marvelx.network_logic.clients.RefreshTokenClientImpl
import com.alexgreece45.marvelx.network_logic.interceptors.ApiInterceptor
import com.alexgreece45.marvelx.network_logic.interceptors.RefreshTokenInterceptor
import com.alexgreece45.marvelx.network_logic.interceptors.WebsocketInterceptor
import com.alexgreece45.marvelx.network_logic.services.RefreshTokenService
import com.alexgreece45.marvelx.network_logic.typeAdapters.ComicPriceTypeAdapter
import com.alexgreece45.marvelx.network_logic.typeAdapters.DeepLinkActionTypeTypeAdapter
import com.alexgreece45.marvelx.network_logic.typeAdapters.InstantTypeAdapter
import com.alexgreece45.marvelx.network_logic.typeAdapters.OffsetDateTimeAdapter
import com.alexgreece45.marvelx.network_logic.typeAdapters.UUIDAdapter
import com.alexgreece45.marvelx.network_logic.typeAdapters.VerificationStepAdapter
import com.alexgreece45.marvelx.network_logic.typeAdapters.WebsocketRequestTypeAdapter
import com.alexgreece45.marvelx.ui.helper.model.DeepLinkActionType
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Single
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Instant
import java.time.OffsetDateTime
import java.util.UUID
import java.util.concurrent.TimeUnit

@Single
@ComponentScan("com.alexgreece45.marvelx.di.network")
class NetworkModule

/**
 * Provides a [Gson] instance with the type adapters for the following types:
 *  - [OffsetDateTime]
 *  - [ComicPriceType]
 *  - [VerificationStep]
 *  - [WebsocketRequestType]
 *  - [UUID]
 *  - [DeepLinkActionType]
 *  - [Instant]
 */
@Single
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
    .registerTypeAdapter(
        WebsocketRequestType::class.java,
        WebsocketRequestTypeAdapter()
    )
    .registerTypeAdapter(
        UUID::class.java,
        UUIDAdapter()
    )
    .registerTypeAdapter(
        DeepLinkActionType::class.java,
        DeepLinkActionTypeTypeAdapter()
    )
    .registerTypeAdapter(
        Instant::class.java,
        InstantTypeAdapter()
    )
    .create()

@Factory
fun provideHttpLoggingInterceptor(
    appLogger: AppLogger,
    configSecurityLogic: ConfigSecurityLogic
): HttpLoggingInterceptor =
    HttpLoggingInterceptor { message ->
        appLogger.d { message }
    }.apply {
        level = when (configSecurityLogic.appBuildType) {
            AppBuildType.RELEASE -> HttpLoggingInterceptor.Level.NONE
            else -> HttpLoggingInterceptor.Level.BODY
        }
    }

/**
 * Provides the [OkHttpClient] for the [ApiClient].
 */
fun provideOkHttpClient(
    context: Context,
    apiAuthenticator: ApiAuthenticator,
    apiInterceptor: ApiInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
    configSecurityLogic: ConfigSecurityLogic,
    configLogic: ConfigLogic
): OkHttpClient {
    val okHttpClient = OkHttpClient.Builder()

    // Set timeouts.
    okHttpClient.connectTimeout(configLogic.backendTimeoutData.connectTimeout, TimeUnit.SECONDS)
    okHttpClient.readTimeout(configLogic.backendTimeoutData.readTimeout, TimeUnit.SECONDS)

    // Adds certificate pins.
    val certificatePinner = CertificatePinner.Builder()
        .add(
            pattern = BuildConfig.MARVELX_BACKEND_DOMAIN,
            pins = arrayOf("sha256/${BuildConfig.MARVELX_BACKEND_DOMAIN_PIN}")
        )
        .build()
    okHttpClient.certificatePinner(certificatePinner)

    // Add authenticator.
    okHttpClient.authenticator(apiAuthenticator)

    // Adds required interceptors.
    okHttpClient.addInterceptor(apiInterceptor)
    if (configSecurityLogic.useNetworkLogger) {
        okHttpClient.addInterceptor(httpLoggingInterceptor)
    }

    // Adds "ChuckerInterceptor" only in non-release builds.
    if (configSecurityLogic.appBuildType !is AppBuildType.RELEASE) {
        okHttpClient.addInterceptor(ChuckerInterceptor(context))
    }

    // Disables response caching for security reasons.
    okHttpClient.cache(null)

    return okHttpClient.build()
}

/**
 * Provides the [OkHttpClient] for the [RefreshTokenClient].
 */
fun provideOkHttpClientForRefreshTokenClient(
    context: Context,
    refreshTokenAuthenticator: RefreshTokenAuthenticator,
    refreshTokenInterceptor: RefreshTokenInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
    configSecurityLogic: ConfigSecurityLogic,
    configLogic: ConfigLogic
): OkHttpClient {
    val okHttpClient = OkHttpClient.Builder()

    // Set timeouts.
    okHttpClient.connectTimeout(configLogic.backendTimeoutData.connectTimeout, TimeUnit.SECONDS)
    okHttpClient.readTimeout(configLogic.backendTimeoutData.readTimeout, TimeUnit.SECONDS)

    // Adds certificate pins.
    val certificatePinner = CertificatePinner.Builder()
        .add(
            pattern = BuildConfig.MARVELX_BACKEND_DOMAIN,
            pins = arrayOf("sha256/${BuildConfig.MARVELX_BACKEND_DOMAIN_PIN}")
        )
        .build()
    okHttpClient.certificatePinner(certificatePinner)

    // Add authenticator.
    okHttpClient.authenticator(refreshTokenAuthenticator)

    // Adds required interceptors.
    okHttpClient.addInterceptor(refreshTokenInterceptor)
    if (configSecurityLogic.useNetworkLogger) {
        okHttpClient.addInterceptor(httpLoggingInterceptor)
    }

    // Adds "ChuckerInterceptor" only in non-release builds.
    if (configSecurityLogic.appBuildType !is AppBuildType.RELEASE) {
        okHttpClient.addInterceptor(ChuckerInterceptor(context))
    }

    // Disables response caching for security reasons.
    okHttpClient.cache(null)

    return okHttpClient.build()
}

/**
 * Provides the [OkHttpClient] for the websocket client.
 */
@Single(binds = [WebsocketControllerImpl::class])
fun provideOkHttpClientForWebsocketClient(
    context: Context,
    httpLoggingInterceptor: HttpLoggingInterceptor,
    websocketInterceptor: WebsocketInterceptor,
    configSecurityLogic: ConfigSecurityLogic,
    configLogic: ConfigLogic
): OkHttpClient {
    val okHttpClient = OkHttpClient.Builder()

    // Set timeouts.
    okHttpClient.connectTimeout(configLogic.websocketTimeoutData.connectTimeout, TimeUnit.SECONDS)
    okHttpClient.readTimeout(configLogic.websocketTimeoutData.readTimeout, TimeUnit.SECONDS)

    // Add certificate pins.
    val certificatePinner = CertificatePinner.Builder()
        .add(
            pattern = BuildConfig.MARVELX_BACKEND_WEBSOCKET_DOMAIN,
            pins = arrayOf("sha256/${BuildConfig.MARVELX_BACKEND_WEBSOCKET_DOMAIN_PIN}")
        )
        .build()
    okHttpClient.certificatePinner(certificatePinner)

    // Add interceptors.
    if (configSecurityLogic.useNetworkLogger) {
        okHttpClient.addInterceptor(httpLoggingInterceptor)
    }
    okHttpClient.addInterceptor(websocketInterceptor)

    // Add "ChuckerInterceptor" only in non-release builds.
    if (configSecurityLogic.appBuildType !is AppBuildType.RELEASE) {
        okHttpClient.addInterceptor(ChuckerInterceptor(context))
    }

    return okHttpClient.build()
}

@Single
fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
    GsonConverterFactory.create(gson)

@Single
fun provideRetrofit(
    okHttpClient: OkHttpClient,
    converterFactory: GsonConverterFactory,
    configLogic: ConfigLogic
): Retrofit = Retrofit.Builder()
    .baseUrl(configLogic.serverUrl)
    .addConverterFactory(converterFactory)
    .client(okHttpClient)
    .build()

/**
 * Provides [Retrofit] for the token service.
 */
fun provideRetrofitForRefreshTokenService(
    okHttpClient: OkHttpClient,
    converterFactory: GsonConverterFactory,
    configLogic: ConfigLogic
): Retrofit = Retrofit.Builder()
    .baseUrl(configLogic.serverUrl)
    .addConverterFactory(converterFactory)
    .client(okHttpClient)
    .build()

@Single(binds = [ApiClientImpl::class])
fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)

@Single(binds = [RefreshTokenClientImpl::class])
fun provideRefreshTokenService(retrofit: Retrofit): RefreshTokenService =
    retrofit.create(RefreshTokenService::class.java)

@Single
fun provideApiClient(apiService: ApiService): ApiClient = ApiClientImpl(apiService)

@Single
fun provideRefreshTokenClient(refreshTokenService: RefreshTokenService): RefreshTokenClient =
    RefreshTokenClientImpl(refreshTokenService)

@Single
fun provideWebsocketController(
    configLogic: ConfigLogic,
    okHttpClient: OkHttpClient,
    authController: AuthController,
    appLogger: AppLogger,
    gson: Gson
): WebsocketController =
    WebsocketControllerImpl(configLogic, okHttpClient, authController, appLogger, gson)

val networkKoinModule = module {
    single<Gson> { provideGson() }
    single<GsonConverterFactory> { provideGsonConverterFactory(get()) }

    single<ApiAuthenticator> { ApiAuthenticator(get(), get(), get(), get(), get(), get(), get()) }
    single<RefreshTokenAuthenticator> { RefreshTokenAuthenticator(get(), get(), get()) }

    single<HttpLoggingInterceptor> { provideHttpLoggingInterceptor(get(), get()) }
    single<ApiInterceptor> { ApiInterceptor(get(), get(), get()) }
    single<RefreshTokenInterceptor> { RefreshTokenInterceptor(get()) }
    single<WebsocketInterceptor> { WebsocketInterceptor(get()) }

    single<ApiService> {
        val okHttpClient: OkHttpClient =
            provideOkHttpClient(get(), get(), get(), get(), get(), get())
        val retrofit: Retrofit = provideRetrofit(okHttpClient, get(), get())
        provideApiService(retrofit)
    }

    single<RefreshTokenService> {
        val okHttpClient: OkHttpClient =
            provideOkHttpClientForRefreshTokenClient(get(), get(), get(), get(), get(), get())
        val retrofit: Retrofit = provideRetrofitForRefreshTokenService(okHttpClient, get(), get())
        provideRefreshTokenService(retrofit)
    }

    single<ApiClient> { provideApiClient(get()) }
    single<RefreshTokenClient> { provideRefreshTokenClient(get()) }

    // Websocket client.
    single<WebsocketController> {
        WebsocketControllerImpl(
            get(),
            provideOkHttpClientForWebsocketClient(get(), get(), get(), get(), get()),
            get(),
            get(),
            get()
        )
    }
}
