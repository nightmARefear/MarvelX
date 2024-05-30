package com.alexgreece45.marvelx.di

import androidx.room.Room
import androidx.work.WorkerFactory
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.MarvelXApplication
import com.alexgreece45.marvelx.RefreshTokenWorkerFactory
import com.alexgreece45.marvelx.configuration.AppBuildType
import com.alexgreece45.marvelx.configuration.ConfigLogic
import com.alexgreece45.marvelx.configuration.ConfigLogicImpl
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogic
import com.alexgreece45.marvelx.configuration.ConfigSecurityLogicImpl
import com.alexgreece45.marvelx.controllers.AppLoggerController
import com.alexgreece45.marvelx.controllers.AppLoggerControllerImpl
import com.alexgreece45.marvelx.controllers.ApplicationPackageController
import com.alexgreece45.marvelx.controllers.ApplicationPackageControllerImpl
import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.AuthControllerImpl
import com.alexgreece45.marvelx.controllers.CryptoController
import com.alexgreece45.marvelx.controllers.CryptoControllerImpl
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.PrefKeysImpl
import com.alexgreece45.marvelx.controllers.PreferencesController
import com.alexgreece45.marvelx.controllers.PreferencesControllerImpl
import com.alexgreece45.marvelx.controllers.RefreshTokenController
import com.alexgreece45.marvelx.controllers.RefreshTokenControllerImpl
import com.alexgreece45.marvelx.controllers.RootController
import com.alexgreece45.marvelx.controllers.RootControllerImpl
import com.alexgreece45.marvelx.controllers.SecurityController
import com.alexgreece45.marvelx.controllers.SecurityControllerImpl
import com.alexgreece45.marvelx.controllers.TimerController
import com.alexgreece45.marvelx.controllers.TimerControllerImpl
import com.alexgreece45.marvelx.database.FavoriteCharactersDao
import com.alexgreece45.marvelx.database.FavoriteCharactersDatabase
import com.alexgreece45.marvelx.network_logic.interceptors.ApiInterceptor
import com.alexgreece45.marvelx.network_logic.interceptors.WebsocketInterceptor
import com.alexgreece45.marvelx.network_logic.networkKoinModule
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
import com.alexgreece45.marvelx.data.repository.ChangePasswordRepository
import com.alexgreece45.marvelx.data.repository.ChangePasswordRepositoryImpl
import com.alexgreece45.marvelx.data.repository.CharacterRepository
import com.alexgreece45.marvelx.data.repository.CharacterRepositoryImpl
import com.alexgreece45.marvelx.data.repository.FavoriteCharactersRepository
import com.alexgreece45.marvelx.data.repository.FavoriteCharactersRepositoryImpl
import com.alexgreece45.marvelx.data.repository.ForgotPasswordRepository
import com.alexgreece45.marvelx.data.repository.ForgotPasswordRepositoryImpl
import com.alexgreece45.marvelx.data.repository.LoginRepository
import com.alexgreece45.marvelx.data.repository.LoginRepositoryImpl
import com.alexgreece45.marvelx.data.repository.OtpRepository
import com.alexgreece45.marvelx.data.repository.OtpRepositoryImpl
import com.alexgreece45.marvelx.data.repository.RegisterRepository
import com.alexgreece45.marvelx.data.repository.RegisterRepositoryImpl
import com.alexgreece45.marvelx.data.repository.UserRepository
import com.alexgreece45.marvelx.data.repository.UserRepositoryImpl
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
import com.alexgreece45.marvelx.ui.screens.home.HomeViewModel
import com.alexgreece45.marvelx.ui.screens.introLoading.IntroLoadingViewModel
import com.alexgreece45.marvelx.ui.screens.login.LoginViewModel
import com.alexgreece45.marvelx.ui.screens.mainActivity.ActivityViewModel
import com.alexgreece45.marvelx.ui.screens.otp.OtpViewModel
import com.alexgreece45.marvelx.ui.screens.profile.ProfileViewModel
import com.alexgreece45.marvelx.ui.screens.signup.SignupViewModel
import com.alexgreece45.marvelx.ui.screens.splash.SplashViewModel
import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.FormValidatorImpl
import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.ResourceProviderImpl
import com.alexgreece45.marvelx.domain.worker.RefreshTokenWorker
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.workmanager.dsl.worker
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
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
            interceptorModule,
            networkModule,
            repositoryModule,
            interactorModule,
            controllerModule,
            formValidatorModule,
            resourceProviderModule,
            configModule,
            viewModelModule,
            workerModule
        )
    }
}

private val navigationModule = module {
    single<RouterHost> { RouterHostImpl() }
}

private val databaseModule = module {
    single<FavoriteCharactersDatabase> {
        Room.databaseBuilder(
            get(),
            FavoriteCharactersDatabase::class.java,
            BuildConfig.DATABASE_NAME
        ).build()
    }

    single<FavoriteCharactersDao> {
        val favoriteCharactersDatabase: FavoriteCharactersDatabase = get()
        favoriteCharactersDatabase.favoriteCharactersDao
    }
}

private val interceptorModule = module {
    single<ApiInterceptor> {
        ApiInterceptor(
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }

    single<WebsocketInterceptor> {
        WebsocketInterceptor(get())
    }
}

//private val networkModule: Module = defaultModule
private val networkModule: Module = networkKoinModule

private val repositoryModule = module {
    single<CharacterRepository> { CharacterRepositoryImpl(get()) }
    single<FavoriteCharactersRepository> { FavoriteCharactersRepositoryImpl(get()) }
    single<LoginRepository> { LoginRepositoryImpl(get(), get(), get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    single<ForgotPasswordRepository> { ForgotPasswordRepositoryImpl(get(), get()) }
    single<ChangePasswordRepository> { ChangePasswordRepositoryImpl(get()) }
    single<OtpRepository> { OtpRepositoryImpl(get(), get()) }
    single<RegisterRepository> { RegisterRepositoryImpl(get()) }
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
            get()
        )
    }
    factory<UserInteractor> { UserInteractorImpl(get(), get(), get(), get()) }
    factory<ForgotPasswordInteractor> { ForgotPasswordInteractorImpl(get(), get()) }
    factory<ChangePasswordInteractor> { ChangePasswordInteractorImpl() }
    factory<OtpInteractor> { OtpInteractorImpl(get(), get(), get()) }
    factory<RegisterInteractor> { RegisterInteractorImpl(get(), get(), get()) }
    factory<SplashInteractor> { SplashInteractorImpl(get()) }
}

private val controllerModule = module {
    single<PreferencesController> { PreferencesControllerImpl(get()) }
    single<PrefKeys> { PrefKeysImpl(get(), get()) }
    single<CryptoController> { CryptoControllerImpl(get(), get(), get()) }
    single<AuthController> { AuthControllerImpl(get(), get()) }
    single<RefreshTokenController> { RefreshTokenControllerImpl(get(), get(), get()) }
    single<TimerController> { TimerControllerImpl() }
    single<RootController> { RootControllerImpl(get()) }
    single<SecurityController> { SecurityControllerImpl(get(), get(), get()) }
    single<ApplicationPackageController> { ApplicationPackageControllerImpl(get(), get()) }
    single<AppLoggerController> { AppLoggerControllerImpl(get()) }
}

private val formValidatorModule = module {
    factory<FormValidator> { FormValidatorImpl() }
}

private val resourceProviderModule = module {
    single<ResourceProvider> { ResourceProviderImpl(get()) }
}

private val configModule = module {
    single<ConfigLogic> { ConfigLogicImpl() }
    single<ConfigSecurityLogic> { ConfigSecurityLogicImpl() }
}

private val viewModelModule = module {
    viewModel { ActivityViewModel() }

    viewModel { SplashViewModel(get(), get()) }
    viewModel { IntroLoadingViewModel(get(), get(), get(), get(), get(), get(), get()) }

    viewModel { HomeViewModel(get()) }
    viewModel { LoginViewModel(get(), get(), get(), get()) }
    viewModel { SignupViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }

    viewModel { OtpViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

    viewModel { ForgotPasswordValidateEmailViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get(), get(), get(), get()) }
    viewModel { ChangePasswordViewModel(get(), get()) }

    viewModel { AllCharactersViewModel(get(), get()) }
    viewModel { AllCharacterComicsViewModel(get()) }
    viewModel { AllFavoriteCharactersViewModel(get()) }
    viewModel { CharacterViewModel(get(), get()) }
    viewModel { ComicViewModel(get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
}

private val workerModule = module {
    worker<RefreshTokenWorker> { RefreshTokenWorker(get(), get(), get(), get(), get(), get()) }
    factory<WorkerFactory> { RefreshTokenWorkerFactory(get(), get(), get(), get()) }
}