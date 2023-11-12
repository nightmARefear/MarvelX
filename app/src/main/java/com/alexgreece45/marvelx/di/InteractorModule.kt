package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.interactors.CharacterInteractor
import com.alexgreece45.marvelx.interactors.CharacterInteractorImpl
import com.alexgreece45.marvelx.interactors.ComicInteractor
import com.alexgreece45.marvelx.interactors.ComicInteractorImpl
import com.alexgreece45.marvelx.interactors.OtpInteractor
import com.alexgreece45.marvelx.interactors.OtpInteractorImpl
import com.alexgreece45.marvelx.interactors.UserInteractor
import com.alexgreece45.marvelx.interactors.UserInteractorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface InteractorModule {

    @Binds
    @ViewModelScoped
    fun bindsCharacterInteractor(impl : CharacterInteractorImpl) : CharacterInteractor

    @Binds
    @ViewModelScoped
    fun bindsComicInteractor(impl : ComicInteractorImpl) : ComicInteractor

    @Binds
    @ViewModelScoped
    fun bindsUserInteractor(impl: UserInteractorImpl): UserInteractor

    @Binds
    @ViewModelScoped
    fun bindsOtpInteractor(impl: OtpInteractorImpl): OtpInteractor
}