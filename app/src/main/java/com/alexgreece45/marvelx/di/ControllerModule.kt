package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.domain.controllers.PreferencesController
import com.alexgreece45.marvelx.domain.controllers.PreferencesControllerImpl
import com.alexgreece45.marvelx.domain.controllers.PreferencesKeys
import com.alexgreece45.marvelx.domain.controllers.PreferencesKeysImpl
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
    fun bindsPreferencesKeys(impl: PreferencesKeysImpl): PreferencesKeys
}