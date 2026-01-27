package com.alexgreece45.marvelx.di

import androidx.room.Room
import androidx.work.WorkerFactory
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.application.MarvelXApplication
import com.alexgreece45.marvelx.application.worker.RefreshTokenWorkerFactory
import com.alexgreece45.marvelx.configuration.AppBuildType
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.configuration.ConfigLogicImpl
import com.alexgreece45.marvelx.configuration.ConfigOauth
import com.alexgreece45.marvelx.configuration.ConfigOauthImpl
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogic
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogicImpl
import com.alexgreece45.marvelx.controllers.AntiHookController
import com.alexgreece45.marvelx.controllers.AntiHookControllerImpl
import com.alexgreece45.marvelx.controllers.AntiReverseEngineeringController
import com.alexgreece45.marvelx.controllers.AntiReverseEngineeringControllerImpl
import com.alexgreece45.marvelx.controllers.AppLogger
import com.alexgreece45.marvelx.controllers.AppLoggerImpl
import com.alexgreece45.marvelx.controllers.ApplicationPackageController
import com.alexgreece45.marvelx.controllers.ApplicationPackageControllerImpl
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.AuthControllerImpl
import com.alexgreece45.marvelx.controllers.CryptoController
import com.alexgreece45.marvelx.controllers.CryptoControllerImpl
import com.alexgreece45.marvelx.controllers.DeepLinkController
import com.alexgreece45.marvelx.controllers.DeepLinkControllerImpl
import com.alexgreece45.marvelx.controllers.DeviceController
import com.alexgreece45.marvelx.controllers.DeviceControllerImpl
import com.alexgreece45.marvelx.controllers.FcmController
import com.alexgreece45.marvelx.controllers.FcmControllerImpl
import com.alexgreece45.marvelx.controllers.MarvelXBackendRequestController
import com.alexgreece45.marvelx.controllers.MarvelXBackendRequestControllerImpl
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.PrefKeysImpl
import com.alexgreece45.marvelx.controllers.PreferencesController
import com.alexgreece45.marvelx.controllers.PreferencesControllerImpl
import com.alexgreece45.marvelx.controllers.RefreshTokenController
import com.alexgreece45.marvelx.controllers.RefreshTokenControllerImpl
import com.alexgreece45.marvelx.controllers.RequestController
import com.alexgreece45.marvelx.controllers.RequestControllerImpl
import com.alexgreece45.marvelx.controllers.RootController
import com.alexgreece45.marvelx.controllers.RootControllerImpl
import com.alexgreece45.marvelx.controllers.SecurityController
import com.alexgreece45.marvelx.controllers.SecurityControllerImpl
import com.alexgreece45.marvelx.controllers.TimerController
import com.alexgreece45.marvelx.controllers.TimerControllerImpl
import com.alexgreece45.marvelx.data.mappers.ListingDataMapper
import com.alexgreece45.marvelx.data.repository.ChangePasswordRepository
import com.alexgreece45.marvelx.data.repository.ChangePasswordRepositoryImpl
import com.alexgreece45.marvelx.data.repository.CharacterRepository
import com.alexgreece45.marvelx.data.repository.CharacterRepositoryImpl
import com.alexgreece45.marvelx.data.repository.FaqsRepository
import com.alexgreece45.marvelx.data.repository.FaqsRepositoryImpl
import com.alexgreece45.marvelx.data.repository.FavoriteCharactersRepository
import com.alexgreece45.marvelx.data.repository.FavoriteCharactersRepositoryImpl
import com.alexgreece45.marvelx.data.repository.ForgotPasswordRepository
import com.alexgreece45.marvelx.data.repository.ForgotPasswordRepositoryImpl
import com.alexgreece45.marvelx.data.repository.ImageRepository
import com.alexgreece45.marvelx.data.repository.ImageRepositoryImpl
import com.alexgreece45.marvelx.data.repository.LoginRepository
import com.alexgreece45.marvelx.data.repository.LoginRepositoryImpl
import com.alexgreece45.marvelx.data.repository.NotificationRepository
import com.alexgreece45.marvelx.data.repository.NotificationRepositoryImpl
import com.alexgreece45.marvelx.data.repository.OtpRepository
import com.alexgreece45.marvelx.data.repository.OtpRepositoryImpl
import com.alexgreece45.marvelx.data.repository.RefreshTokenRepository
import com.alexgreece45.marvelx.data.repository.RefreshTokenRepositoryImpl
import com.alexgreece45.marvelx.data.repository.RegisterRepository
import com.alexgreece45.marvelx.data.repository.RegisterRepositoryImpl
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.data.repository.UserRepositoryImpl
import com.alexgreece45.marvelx.data.repository.imageLoader.AuthenticatedImageLoader
import com.alexgreece45.marvelx.data.repository.marketplace.MarketplaceRepository
import com.alexgreece45.marvelx.data.repository.marketplace.MarketplaceRepositoryImpl
import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingCategoryRepository
import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingCategoryRepositoryImpl
import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingRepository
import com.alexgreece45.marvelx.data.repository.marketplace.listing.ListingRepositoryImpl
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepository
import com.alexgreece45.marvelx.data.repository.marketplace.listing.details.ListingDetailsRepositoryImpl
import com.alexgreece45.marvelx.data.repository.user.BlockedUserRepository
import com.alexgreece45.marvelx.data.repository.user.BlockedUserRepositoryImpl
import com.alexgreece45.marvelx.data.repository.user.FriendRepository
import com.alexgreece45.marvelx.data.repository.user.FriendRepositoryImpl
import com.alexgreece45.marvelx.data.repository.user.FriendRequestRepository
import com.alexgreece45.marvelx.data.repository.user.FriendRequestRepositoryImpl
import com.alexgreece45.marvelx.database.FavoriteCharactersDao
import com.alexgreece45.marvelx.database.ListingCategoryDao
import com.alexgreece45.marvelx.database.MarvelXDatabase
import com.alexgreece45.marvelx.dispatchers.DefaultDispatcher
import com.alexgreece45.marvelx.dispatchers.IoDispatcher
import com.alexgreece45.marvelx.dispatchers.dispatchersModule
import com.alexgreece45.marvelx.domain.interactors.ChangePasswordInteractor
import com.alexgreece45.marvelx.domain.interactors.ChangePasswordInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.ComicInteractor
import com.alexgreece45.marvelx.domain.interactors.ComicInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.ForgotPasswordInteractor
import com.alexgreece45.marvelx.domain.interactors.ForgotPasswordInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.LoginInteractor
import com.alexgreece45.marvelx.domain.interactors.LoginInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.OtpInteractor
import com.alexgreece45.marvelx.domain.interactors.OtpInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.RegisterInteractor
import com.alexgreece45.marvelx.domain.interactors.RegisterInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.SplashInteractor
import com.alexgreece45.marvelx.domain.interactors.SplashInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.UserInteractor
import com.alexgreece45.marvelx.domain.interactors.UserInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.marketplace.MarketplaceInteractor
import com.alexgreece45.marvelx.domain.interactors.marketplace.MarketplaceInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.marketplace.listing.ListingInteractor
import com.alexgreece45.marvelx.domain.interactors.marketplace.listing.ListingInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.user.BlockedUserInteractor
import com.alexgreece45.marvelx.domain.interactors.user.BlockedUserInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.user.FriendInteractor
import com.alexgreece45.marvelx.domain.interactors.user.FriendInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.user.FriendRequestInteractor
import com.alexgreece45.marvelx.domain.interactors.user.FriendRequestInteractorImpl
import com.alexgreece45.marvelx.domain.mappers.marketplace.listing.ListingDomainMapper
import com.alexgreece45.marvelx.domain.mappers.marketplace.listing.details.ListingDetailsDomainMapper
import com.alexgreece45.marvelx.domain.worker.RefreshTokenWorker
import com.alexgreece45.marvelx.network_logic.di.networkKoinModule
import com.alexgreece45.marvelx.network_logic.di.provideOkHttpClient
import com.alexgreece45.marvelx.ui.helper.FieldValidationRules
import com.alexgreece45.marvelx.ui.helper.FieldValidationRulesImpl
import com.alexgreece45.marvelx.ui.navigation.router.RouterHost
import com.alexgreece45.marvelx.ui.navigation.router.RouterHostImpl
import com.alexgreece45.marvelx.ui.screens.allCharacters.AllCharactersViewModel
import com.alexgreece45.marvelx.ui.screens.allFavoriteCharacters.AllFavoriteCharactersViewModel
import com.alexgreece45.marvelx.ui.screens.changePassword.ChangePasswordViewModel
import com.alexgreece45.marvelx.ui.screens.character.CharacterViewModel
import com.alexgreece45.marvelx.ui.screens.character.allCharacterComicsScreen.AllCharacterComicsViewModel
import com.alexgreece45.marvelx.ui.screens.comic.ComicViewModel
import com.alexgreece45.marvelx.ui.screens.forgotPasswordScreen.ForgotPasswordViewModel
import com.alexgreece45.marvelx.ui.screens.forgotPasswordScreen.forgotPasswordValidateEmailScreen.ForgotPasswordValidateEmailViewModel
import com.alexgreece45.marvelx.ui.screens.friends.FriendsViewModel
import com.alexgreece45.marvelx.ui.screens.home.HomeViewModel
import com.alexgreece45.marvelx.ui.screens.introLoading.IntroLoadingViewModel
import com.alexgreece45.marvelx.ui.screens.login.LoginViewModel
import com.alexgreece45.marvelx.ui.screens.mainActivity.MainActivityViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.MarketplaceViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.CreateListingWizardViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.advancedDetails.CreateListingAdvancedDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.createListingWizard.basicDetails.CreateListingBasicDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.listingDetailsScreen.ListingDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.ListingDetailsUiMapper
import com.alexgreece45.marvelx.ui.screens.marketplace.mappers.ListingUiMapper
import com.alexgreece45.marvelx.ui.screens.myListings.MyListingsViewModel
import com.alexgreece45.marvelx.ui.screens.otp.OtpViewModel
import com.alexgreece45.marvelx.ui.screens.profile.ProfileViewModel
import com.alexgreece45.marvelx.ui.screens.profile.editProfileDetails.EditProfileDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.profile.image.ProfileImageViewModel
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.ProfileDetailsViewModel
import com.alexgreece45.marvelx.ui.screens.profile.profileDetails.stats.ProfileDetailsStatsViewModel
import com.alexgreece45.marvelx.ui.screens.qrCode.QrCodeScanViewModel
import com.alexgreece45.marvelx.ui.screens.settings.SettingsViewModel
import com.alexgreece45.marvelx.ui.screens.settings.faq.FaqViewModel
import com.alexgreece45.marvelx.ui.screens.settings.privacySettings.PrivacySettingsViewModel
import com.alexgreece45.marvelx.ui.screens.signup.SignupViewModel
import com.alexgreece45.marvelx.ui.screens.splash.SplashViewModel
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.FormValidatorImpl
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.ResourceProviderImpl
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module

/**
 * Initializes a Koin application with the given [marvelXApplication] as the Android Context used
 * for injection purposes, and also provides all the necessary [Module]s.
 */
fun initKoin(marvelXApplication: MarvelXApplication) {
    startKoin {
        androidLogger(if (BuildConfig.BUILD_TYPE == AppBuildType.DEBUG.key) Level.DEBUG else Level.NONE)
        androidContext(marvelXApplication)

        workManagerFactory()

        modules(
            navigationModule,
            databaseModule,
            networkKoinModule,
            repositoryModule,
            interactorModule,
            mapperModule,
            controllerModule,
            formValidatorModule,
            resourceProviderModule,
            configModule,
            viewModelModule,
            workerModule,
            dispatchersModule,
        )
    }
}

private val navigationModule = module {
    single<RouterHost> { RouterHostImpl() }
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

private val repositoryModule = module {
    single<CharacterRepository> { CharacterRepositoryImpl(get()) }
    single<FavoriteCharactersRepository> { FavoriteCharactersRepositoryImpl(get()) }
    single<RefreshTokenRepository> { RefreshTokenRepositoryImpl(get()) }
    single<LoginRepository> { LoginRepositoryImpl(get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get(), get(qualifier = qualifier<IoDispatcher>())) }
    single<FaqsRepository> { FaqsRepositoryImpl(get(), get(qualifier = qualifier<IoDispatcher>())) }
    single<ForgotPasswordRepository> { ForgotPasswordRepositoryImpl(get(), get()) }
    single<ChangePasswordRepository> { ChangePasswordRepositoryImpl(get()) }
    single<OtpRepository> { OtpRepositoryImpl(get(), get()) }
    single<RegisterRepository> { RegisterRepositoryImpl(get()) }

    // Image loader.
    single<AuthenticatedImageLoader> {
        AuthenticatedImageLoader(
            context = get(),
            okHttpClient = provideOkHttpClient(get(), get(), get(), get(), get(), get())
        )
    }

    // User repositories.
    single<FriendRepository> {
        FriendRepositoryImpl(
            get(),
            get(qualifier = qualifier<IoDispatcher>())
        )
    }
    single<FriendRequestRepository> {
        FriendRequestRepositoryImpl(
            get(),
            get(qualifier = qualifier<IoDispatcher>())
        )
    }
    single<BlockedUserRepository> {
        BlockedUserRepositoryImpl(
            get(),
            get(qualifier = qualifier<IoDispatcher>())
        )
    }

    single<ImageRepository> { ImageRepositoryImpl(get(), get(qualifier<IoDispatcher>())) }
    single<NotificationRepository> {
        NotificationRepositoryImpl(
            get(),
            get(qualifier<IoDispatcher>())
        )
    }

    // Marketplace repositories.
    single<MarketplaceRepository> {
        MarketplaceRepositoryImpl(get(), get(qualifier<IoDispatcher>()))
    }
    single<ListingRepository> {
        ListingRepositoryImpl(
            get(),
            get(qualifier<IoDispatcher>()),
            get()
        )
    }
    single<ListingCategoryRepository> {
        ListingCategoryRepositoryImpl(
            get(),
            get(),
            get(qualifier<IoDispatcher>()),
            get()
        )
    }
    single<ListingDetailsRepository> {
        ListingDetailsRepositoryImpl(get(), get(qualifier<IoDispatcher>()), get())
    }
}

private val interactorModule = module {
    factory<CharacterInteractor> { CharacterInteractorImpl(get(), get(), get(), get()) }
    factory<ComicInteractor> { ComicInteractorImpl(get(), get(), get()) }
    factory<LoginInteractor> {
        LoginInteractorImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    factory<UserInteractor> { UserInteractorImpl(get(), get(), get(), get()) }
    factory<ForgotPasswordInteractor> { ForgotPasswordInteractorImpl(get(), get()) }
    factory<ChangePasswordInteractor> { ChangePasswordInteractorImpl() }
    factory<OtpInteractor> { OtpInteractorImpl(get(), get(), get()) }
    factory<RegisterInteractor> { RegisterInteractorImpl(get(), get(), get()) }
    factory<SplashInteractor> { SplashInteractorImpl(get()) }

    // User interactors.
    factory<FriendInteractor> { FriendInteractorImpl(get()) }
    factory<FriendRequestInteractor> { FriendRequestInteractorImpl(get()) }
    factory<BlockedUserInteractor> { BlockedUserInteractorImpl(get(), get(), get(), get()) }

    // Marketplace interactors.
    factory<MarketplaceInteractor> { MarketplaceInteractorImpl(get()) }
    factory<ListingInteractor> { ListingInteractorImpl(get(), get()) }
}

private val mapperModule = module {
    single<ListingDataMapper> { ListingDataMapper(get()) }
    single<ListingDomainMapper> { ListingDomainMapper(get()) }
    single<ListingDetailsDomainMapper> { ListingDetailsDomainMapper(get()) }
    single<ListingDetailsUiMapper> { ListingDetailsUiMapper(get(), get()) }
    single<ListingUiMapper> { ListingUiMapper(get(), get(), get()) }
}

private val controllerModule = module {
    single<PreferencesController> { PreferencesControllerImpl(get()) }
    single<PrefKeys> { PrefKeysImpl(get(), get()) }
    single<CryptoController> { CryptoControllerImpl(get(), get()) }
    single<AuthController> { AuthControllerImpl(get(), get(), get(), get()) }
    single<RefreshTokenController> { RefreshTokenControllerImpl(get(), get(), get()) }
    single<TimerController> { TimerControllerImpl() }
    single<RootController> { RootControllerImpl(get()) }
    single<SecurityController> {
        SecurityControllerImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(qualifier<IoDispatcher>())
        )
    }
    single<ApplicationPackageController> { ApplicationPackageControllerImpl(get(), get()) }
    single<AppLogger> { AppLoggerImpl(get()) }
    single<AntiReverseEngineeringController> { AntiReverseEngineeringControllerImpl(get(), get()) }
    single<AntiHookController> { AntiHookControllerImpl() }
    single<RequestController> { RequestControllerImpl(get(), get(), get()) }
    single<MarvelXBackendRequestController> {
        MarvelXBackendRequestControllerImpl(
            get(),
            get(),
            get()
        )
    }
    single<DeepLinkController> { DeepLinkControllerImpl(get(), get()) }
    single<FcmController> { FcmControllerImpl(get()) }
    single<DeviceController> {
        DeviceControllerImpl(
            get(),
            get(),
            get(qualifier = qualifier<IoDispatcher>())
        )
    }
}

private val formValidatorModule = module {
    single<FieldValidationRules> { FieldValidationRulesImpl(get()) }
    factory<FormValidator> { FormValidatorImpl() }
}

private val resourceProviderModule = module {
    single<ResourceProvider> { ResourceProviderImpl(get()) }
}

private val configModule = module {
    single<ConfigLogic> { ConfigLogicImpl() }
    single<ConfigSecurityLogic> { ConfigSecurityLogicImpl() }
    single<ConfigOauth> { ConfigOauthImpl() }
}

private val viewModelModule = module {
    viewModelOf(::MainActivityViewModel)

    viewModel { SplashViewModel(get(), get()) }
    viewModel { IntroLoadingViewModel(get(), get(), get(), get(), get(), get(), get()) }

    viewModel { HomeViewModel(get(), get()) }
    viewModel {
        LoginViewModel(
            get(),
            get(),
            get(),
            get(),
            get(qualifier = qualifier<DefaultDispatcher>())
        )
    }
    viewModel {
        SignupViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(qualifier = qualifier<DefaultDispatcher>())
        )
    }

    viewModelOf(::OtpViewModel)

    viewModel { ForgotPasswordValidateEmailViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get(), get(), get(), get()) }
    viewModel { ChangePasswordViewModel(get(), get()) }

    // Marketplace.
    viewModelOf(::MarketplaceViewModel)
    viewModelOf(::ListingDetailsViewModel)
    viewModelOf(::CreateListingWizardViewModel)
    viewModelOf(::CreateListingBasicDetailsViewModel)
    viewModelOf(::CreateListingAdvancedDetailsViewModel)

    viewModelOf(::MyListingsViewModel)

    viewModel { AllCharactersViewModel(get(), get()) }
    viewModel { AllCharacterComicsViewModel(get()) }
    viewModel { AllFavoriteCharactersViewModel(get()) }
    viewModel { CharacterViewModel(get(), get()) }
    viewModelOf(::ComicViewModel)

    viewModelOf(::FriendsViewModel)

    viewModelOf(::ProfileViewModel)
    viewModelOf(::ProfileImageViewModel)
    viewModelOf(::ProfileDetailsViewModel)

    viewModel { EditProfileDetailsViewModel(get(), get(), get(), get()) }
    viewModel { ProfileDetailsStatsViewModel(get(), get()) }
    viewModel { PrivacySettingsViewModel(get(), get()) }
    viewModel { FaqViewModel(get(), get()) }
    viewModel { QrCodeScanViewModel() }

    viewModelOf(::SettingsViewModel)
}

private val workerModule = module {
    worker<RefreshTokenWorker> {
        RefreshTokenWorker(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(qualifier = qualifier<IoDispatcher>())
        )
    }
    factory<WorkerFactory> {
        RefreshTokenWorkerFactory(
            get(),
            get(),
            get(),
            get(),
            get(qualifier = qualifier<IoDispatcher>())
        )
    }
}
