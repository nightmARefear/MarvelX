package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.domain.interactors.CharacterInteractor
import com.alexgreece45.marvelx.domain.interactors.CharacterInteractorImpl
import com.alexgreece45.marvelx.domain.interactors.ComicInteractor
import com.alexgreece45.marvelx.domain.interactors.ComicInteractorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ViewModelComponent::class)
interface InteractorModule {

    @Binds
    @ViewModelScoped
    fun bindsCharacterInteractor(impl : CharacterInteractorImpl) : CharacterInteractor

    @Binds
    @ViewModelScoped
    fun bindsComicInteractor(impl : ComicInteractorImpl) : ComicInteractor
}