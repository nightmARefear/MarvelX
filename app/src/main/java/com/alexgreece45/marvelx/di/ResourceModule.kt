package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.util.ResourceProvider
import com.alexgreece45.marvelx.util.ResourceProviderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ResourceModule {

    @Provides
    @Singleton
    fun provideResourceProvider(impl : ResourceProviderImpl) : ResourceProvider = impl
}