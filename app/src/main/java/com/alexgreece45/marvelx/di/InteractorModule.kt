package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.interactors.ChangePasswordInteractor
import com.alexgreece45.marvelx.interactors.ChangePasswordInteractorImpl
import com.alexgreece45.marvelx.interactors.CharacterInteractor
import com.alexgreece45.marvelx.interactors.CharacterInteractorImpl
import com.alexgreece45.marvelx.interactors.ComicInteractor
import com.alexgreece45.marvelx.interactors.ComicInteractorImpl
import com.alexgreece45.marvelx.interactors.ForgotPasswordInteractor
import com.alexgreece45.marvelx.interactors.ForgotPasswordInteractorImpl
import com.alexgreece45.marvelx.interactors.OtpInteractor
import com.alexgreece45.marvelx.interactors.OtpInteractorImpl
import com.alexgreece45.marvelx.interactors.RegisterInteractor
import com.alexgreece45.marvelx.interactors.RegisterInteractorImpl
import com.alexgreece45.marvelx.interactors.SplashInteractor
import com.alexgreece45.marvelx.interactors.SplashInteractorImpl
import com.alexgreece45.marvelx.interactors.LoginInteractor
import com.alexgreece45.marvelx.interactors.LoginInteractorImpl
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
    fun bindsCharacterInteractor(impl: CharacterInteractorImpl): CharacterInteractor

    @Binds
    @ViewModelScoped
    fun bindsComicInteractor(impl: ComicInteractorImpl): ComicInteractor

    @Binds
    @ViewModelScoped
    fun bindsLoginInteractor(impl: LoginInteractorImpl): LoginInteractor

    @Binds
    @ViewModelScoped
    fun bindsUserInteractor(impl: UserInteractorImpl): UserInteractor

    @Binds
    @ViewModelScoped
    fun bindsForgotPasswordInteractor(impl: ForgotPasswordInteractorImpl): ForgotPasswordInteractor

    @Binds
    @ViewModelScoped
    fun bindsChangePasswordInteractor(impl: ChangePasswordInteractorImpl): ChangePasswordInteractor

    @Binds
    @ViewModelScoped
    fun bindsOtpInteractor(impl: OtpInteractorImpl): OtpInteractor

    @Binds
    @ViewModelScoped
    fun bindsRegisterInteractor(impl: RegisterInteractorImpl): RegisterInteractor

    @Binds
    @ViewModelScoped
    fun bindsSplashInteractor(impl: SplashInteractorImpl): SplashInteractor
}