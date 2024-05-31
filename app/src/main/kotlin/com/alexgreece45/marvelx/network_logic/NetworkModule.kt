package com.alexgreece45.marvelx.network_logic

import android.content.Context
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.configuration.AppBuildType
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogic
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.CryptoController
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.RequestController
import com.alexgreece45.marvelx.controllers.RequestControllerImpl
import com.alexgreece45.marvelx.controllers.WebsocketController
import com.alexgreece45.marvelx.controllers.WebsocketControllerImpl
import com.alexgreece45.marvelx.controllers.model.websocket.WebsocketRequestType
import com.alexgreece45.marvelx.network_logic.api.ApiClient
import com.alexgreece45.marvelx.network_logic.api.ApiService
import com.alexgreece45.marvelx.di.ApiClientImpl
import com.alexgreece45.marvelx.network_logic.interceptors.ApiInterceptor
import com.alexgreece45.marvelx.network_logic.interceptors.WebsocketInterceptor
import com.alexgreece45.marvelx.domain.models_api.comic.ComicPriceType
import com.alexgreece45.marvelx.domain.models_api.user.VerificationStep
import com.alexgreece45.marvelx.network_logic.typeAdapters.ComicPriceTypeAdapter
import com.alexgreece45.marvelx.network_logic.typeAdapters.OffsetDateTimeAdapter
import com.alexgreece45.marvelx.network_logic.typeAdapters.UUIDAdapter
import com.alexgreece45.marvelx.network_logic.typeAdapters.VerificationStepAdapter
import com.alexgreece45.marvelx.network_logic.typeAdapters.WebsocketRequestTypeAdapter
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
import java.time.OffsetDateTime
import java.util.UUID

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
    .create()

@Factory
fun provideHttpLoggingInterceptor(
    appLoggerController: AppLoggerController,
    configSecurityLogic: ConfigSecurityLogic
): HttpLoggingInterceptor =
    HttpLoggingInterceptor { message ->
        appLoggerController.d { message }
    }.apply {
        level = when (configSecurityLogic.appBuildType) {
            AppBuildType.RELEASE -> HttpLoggingInterceptor.Level.NONE
            else -> HttpLoggingInterceptor.Level.BODY
        }
    }

fun provideOkHttpClient(
    context: Context,
    apiInterceptor: ApiInterceptor,
    httpLoggingInterceptor: HttpLoggingInterceptor,
    configSecurityLogic: ConfigSecurityLogic
): OkHttpClient {
    val okHttpClient = OkHttpClient.Builder()

    // Adds certificate pins.
    val certificatePinner = CertificatePinner.Builder()
        .add(
            pattern = BuildConfig.MARVELX_BACKEND_DOMAIN,
            pins = arrayOf("sha256/${BuildConfig.MARVELX_BACKEND_DOMAIN_PIN}")
        )
        .build()
    okHttpClient.certificatePinner(certificatePinner)

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
 * Provides the [OkHttpClient] for the websocket client.
 */
@Single(binds = [WebsocketControllerImpl::class])
fun provideOkHttpClientForWebsocketClient(
    context: Context,
    httpLoggingInterceptor: HttpLoggingInterceptor,
    websocketInterceptor: WebsocketInterceptor,
    configSecurityLogic: ConfigSecurityLogic
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

@Single(binds = [ApiClientImpl::class])
fun provideApiService(retrofit: Retrofit): ApiService =
    retrofit.create(ApiService::class.java)

@Single
fun provideApiClient(apiService: ApiService): ApiClient = ApiClientImpl(apiService)

@Single
fun provideRequestController(
    appLoggerController: AppLoggerController,
    cryptoController: CryptoController,
    prefKeys: PrefKeys
): RequestController = RequestControllerImpl(appLoggerController, cryptoController, prefKeys)

@Single
fun provideWebsocketController(
    configLogic: ConfigLogic,
    okHttpClient: OkHttpClient,
    authController: AuthController,
    appLoggerController: AppLoggerController,
    gson: Gson
): WebsocketController =
    WebsocketControllerImpl(configLogic, okHttpClient, authController, appLoggerController, gson)

val networkKoinModule = module {
    single<Gson> { provideGson() }
    single<GsonConverterFactory> { provideGsonConverterFactory(get()) }
    single<HttpLoggingInterceptor> { provideHttpLoggingInterceptor(get(), get()) }
    single<OkHttpClient> { provideOkHttpClient(get(), get(), get(), get()) }
    single<Retrofit> { provideRetrofit(get(), get(), get()) }
    single<RequestController> { RequestControllerImpl(get(), get(), get()) }

    single<ApiService> { provideApiService(get()) }

    single<ApiClient> { provideApiClient(get()) }

    // Websocket client.
    single<WebsocketController> {
        WebsocketControllerImpl(
            get(),
            provideOkHttpClientForWebsocketClient(get(), get(), get(), get()),
            get(),
            get(),
            provideGson()
        )
    }
}