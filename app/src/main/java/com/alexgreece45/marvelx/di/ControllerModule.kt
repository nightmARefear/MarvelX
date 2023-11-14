package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.controllers.AuthController
import com.alexgreece45.marvelx.controllers.AuthControllerImpl
import com.alexgreece45.marvelx.controllers.ConfigController
import com.alexgreece45.marvelx.controllers.ConfigControllerImpl
import com.alexgreece45.marvelx.controllers.PreferencesController
import com.alexgreece45.marvelx.controllers.PreferencesControllerImpl
import com.alexgreece45.marvelx.controllers.PrefKeys
import com.alexgreece45.marvelx.controllers.PrefKeysImpl
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
    fun bindsConfigController(impl: ConfigControllerImpl): ConfigController

    @Binds
    @Singleton
    fun bindsAuthController(impl: AuthControllerImpl): AuthController
}