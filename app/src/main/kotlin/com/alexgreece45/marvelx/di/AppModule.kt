package com.alexgreece45.marvelx.di

import androidx.room.Room
import androidx.work.WorkerFactory
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.application.MarvelXApplication
import com.alexgreece45.marvelx.application.worker.RefreshTokenWorkerFactory
import com.alexgreece45.marvelx.common_ui.di.CommonUiModule
import com.alexgreece45.marvelx.configuration.di.ConfigurationModule
import com.alexgreece45.marvelx.controllers.di.ControllersModule
import com.alexgreece45.marvelx.core.common.config.model.AppBuildType
import com.alexgreece45.marvelx.core.common.di.CoreCommonModule
import com.alexgreece45.marvelx.core.data.di.CoreDataModule
import com.alexgreece45.marvelx.core.datastore.di.CoreDatastoreModule
import com.alexgreece45.marvelx.core.network.di.CoreNetworkModule
import com.alexgreece45.marvelx.core.security.di.CoreSecurityModule
import com.alexgreece45.marvelx.data.di.DataModule
import com.alexgreece45.marvelx.database.FavoriteCharactersDao
import com.alexgreece45.marvelx.database.ListingCategoryDao
import com.alexgreece45.marvelx.database.MarvelXDatabase
import com.alexgreece45.marvelx.domain.di.DomainModule
import com.alexgreece45.marvelx.domain.mappers.cart.CartSummaryDomainMapper
import com.alexgreece45.marvelx.domain.mappers.marketplace.listing.ListingDomainMapper
import com.alexgreece45.marvelx.domain.mappers.marketplace.listing.details.ListingDetailsDomainMapper
import com.alexgreece45.marvelx.domain.mappers.marketplace.listing.details.ListingDetailsResponseMapper
import com.alexgreece45.marvelx.domain.worker.RefreshTokenWorker
import com.alexgreece45.marvelx.notifications.di.NotificationsModule
import com.alexgreece45.marvelx.ui.helper.FieldValidationRules
import com.alexgreece45.marvelx.ui.helper.FieldValidationRulesImpl
import com.alexgreece45.marvelx.ui.screens.cart.mappers.CartItemUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.FilteredListingUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.ListingDetailsUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.ListingPreviewUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.ListingUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.MarketplaceUiMapper
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.FormValidatorImpl
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.ResourceProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.annotation.KoinApplication
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ksp.generated.module

@KoinApplication
class MarvelXApp

/**
 * Initializes a Koin application with the given [marvelXApplication] as the Android Context used
 * for injection purposes, and also provides all the necessary [Module]s.
 */
fun initKoin(marvelXApplication: MarvelXApplication) {
    startKoin {
//    startKoin<MarvelXApp> {
        androidLogger(if (BuildConfig.BUILD_TYPE == AppBuildType.DEBUG.key) Level.DEBUG else Level.NONE)
        androidContext(marvelXApplication)

        workManagerFactory()

        modules(
            databaseModule,

            ConfigurationModule().module,

            CoreCommonModule().module,
            CoreNetworkModule().module,
            CoreDatastoreModule().module,
            CoreSecurityModule().module,
            CoreDataModule().module,

            DataModule().module,
            DomainModule().module,
            mapperModule,
            ControllersModule().module,
            formValidatorModule,
            resourceProviderModule,
            workerModule,
            CommonUiModule().module,
            NotificationsModule().module,
            UiModule().module,
        )
//        monitoring()
    }
}

private val databaseModule = module {
    single<MarvelXDatabase> {
        Room.databaseBuilder(
            get(),
            MarvelXDatabase::class.java,
            BuildConfig.DATABASE_NAME
        ).build()
    }

    single<FavoriteCharactersDao> { get<MarvelXDatabase>().favoriteCharactersDao }
    single<ListingCategoryDao> { get<MarvelXDatabase>().listingCategoryDao }
}

private val mapperModule = module {
//    single<ListingDataMapper> { ListingDataMapper(get()) }
    single<ListingDomainMapper> { ListingDomainMapper(get()) }
    single<ListingDetailsDomainMapper> { ListingDetailsDomainMapper(get()) }
    singleOf(::ListingDetailsUiMapper)
    singleOf(::ListingUiMapper)
    singleOf(::ListingPreviewUiMapper)
    singleOf(::MarketplaceUiMapper)
    singleOf(::CartItemUiMapper)
    singleOf(::CartSummaryDomainMapper)
    singleOf(::FilteredListingUiMapper)
    factoryOf(::ListingDetailsResponseMapper)
}

private val formValidatorModule = module {
    single<FieldValidationRules> { FieldValidationRulesImpl(get()) }
    factory<FormValidator> { FormValidatorImpl() }
}

private val resourceProviderModule = module {
    single<ResourceProvider> { ResourceProviderImpl(get()) }
}

private val viewModelModule = module {
//    viewModelOf(::MainActivityViewModel)

//    viewModel { SplashViewModel(get(), get()) }
//    viewModel { IntroLoadingViewModel(get(), get(), get(), get(), get(), get(), get()) }

//    viewModel { HomeViewModel(get()) }
//    viewModelOf(::LoginViewModel)
//    viewModelOf(::SignupViewModel)

//    viewModelOf(::OtpViewModel)

//    viewModel { ForgotPasswordValidateEmailViewModel(get()) }
//    viewModel { ForgotPasswordViewModel(get(), get(), get(), get()) }
//    viewModel { ChangePasswordViewModel(get(), get()) }

    // Marketplace.
//    viewModelOf(::MarketplaceViewModel)
//    viewModelOf(::ListingDetailsViewModel)
//    viewModelOf(::CreateListingWizardViewModel)
//    viewModelOf(::CreateListingBasicDetailsViewModel)
//    viewModelOf(::CreateListingAdvancedDetailsViewModel)
//    viewModelOf(::CreateListingSuccessViewModel)

//    viewModelOf(::MyListingsViewModel)

//    viewModelOf(::CartViewModel)

//    viewModelOf(::CheckoutWizardViewModel)
//    viewModelOf(::CheckoutShippingDetailsViewModel)
//    viewModelOf(::CheckoutPaymentDetailsViewModel)
//    viewModelOf(::CheckoutSuccessViewModel)

//    viewModelOf(::OrdersViewModel)
//    viewModelOf(::OrderDetailsViewModel)
//    viewModelOf(::OrderItemTrackingHistoryViewModel)
//    viewModelOf(::RequestItemReturnViewModel)

//    viewModel { AllCharactersViewModel(get(), get()) }
//    viewModel { AllCharacterComicsViewModel(get()) }
//    viewModel { AllFavoriteCharactersViewModel(get()) }
//    viewModel { CharacterViewModel(get(), get()) }
//    viewModelOf(::ComicViewModel)

//    viewModelOf(::FriendsViewModel)

//    viewModelOf(::ProfileViewModel)
//    viewModelOf(::ProfileImageViewModel)
//    viewModelOf(::ProfileDetailsViewModel)

//    viewModel { EditProfileDetailsViewModel(get(), get(), get(), get()) }
//    viewModel { ProfileDetailsStatsViewModel(get(), get()) }
//    viewModel { PrivacySettingsViewModel(get(), get()) }
//    viewModel { FaqViewModel(get(), get()) }
//    viewModelOf(::QrCodeScanViewModel)

//    viewModelOf(::SettingsViewModel)
}

private val workerModule = module {
    worker<RefreshTokenWorker> {
        RefreshTokenWorker(
            get(),
            get(),
            get(),
            get(),
            get(),
        )
    }
    factory<WorkerFactory> {
        RefreshTokenWorkerFactory(
            get(),
            get(),
            get(),
        )
    }
}

// TODO: Fix module.
//private val customScopesModule = module {
//    scope<CreateListingWizardViewModel> {
//        scoped<CreateListingWizardController> { CreateListingWizardController() }
//    }
//}