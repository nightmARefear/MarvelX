package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.data.api.ApiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiClientModule {

    @Provides
    @Singleton
    fun provideApiClient(impl : ApiClientImpl) : ApiClient = impl
}