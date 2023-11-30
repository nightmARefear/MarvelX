package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.AuthControllerImpl
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
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ControllerModule {

    @Binds
    @Singleton
    fun bindsPreferencesController(impl: PreferencesControllerImpl): PreferencesController

    @Binds
    @Singleton
    fun bindsPreferencesKeys(impl: PrefKeysImpl): PrefKeys

    @Binds
    @Singleton
    fun bindsAuthController(impl: AuthControllerImpl): AuthController

    @Binds
    @Singleton
    fun bindsRefreshTokenController(impl: RefreshTokenControllerImpl): RefreshTokenController

    @Binds
    @Singleton
    fun bindsTimerController(impl: TimerControllerImpl): TimerController

    @Binds
    @Singleton
    fun bindsRootController(impl: RootControllerImpl): RootController

    @Binds
    @Singleton
    fun bindsSecurityController(impl: SecurityControllerImpl): SecurityController
}