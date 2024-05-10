package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.repository.ChangePasswordRepository
import com.alexgreece45.marvelx.repository.ChangePasswordRepositoryImpl
import com.alexgreece45.marvelx.repository.CharacterRepository
import com.alexgreece45.marvelx.repository.CharacterRepositoryImpl
import com.alexgreece45.marvelx.repository.FavoriteCharactersRepository
import com.alexgreece45.marvelx.repository.FavoriteCharactersRepositoryImpl
import com.alexgreece45.marvelx.repository.ForgotPasswordRepository
import com.alexgreece45.marvelx.repository.ForgotPasswordRepositoryImpl
import com.alexgreece45.marvelx.repository.LoginRepository
import com.alexgreece45.marvelx.repository.LoginRepositoryImpl
import com.alexgreece45.marvelx.repository.OtpRepository
import com.alexgreece45.marvelx.repository.OtpRepositoryImpl
import com.alexgreece45.marvelx.repository.RegisterRepository
import com.alexgreece45.marvelx.repository.RegisterRepositoryImpl
import com.alexgreece45.marvelx.repository.UserRepository
import com.alexgreece45.marvelx.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

    @Binds
    fun bindsCharacterRepository(impl: CharacterRepositoryImpl): CharacterRepository

    @Binds
    fun bindsFavoriteCharactersRepository(impl: FavoriteCharactersRepositoryImpl): FavoriteCharactersRepository

    @Binds
    fun bindsLoginRepository(impl: LoginRepositoryImpl): LoginRepository

    @Binds
    fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    fun bindsForgotPasswordRepository(impl: ForgotPasswordRepositoryImpl): ForgotPasswordRepository

    @Binds
    fun bindsChangePasswordRepository(impl: ChangePasswordRepositoryImpl): ChangePasswordRepository

    @Binds
    fun bindsOtpRepository(impl: OtpRepositoryImpl): OtpRepository

    @Binds
    fun bindsRegisterRepository(impl: RegisterRepositoryImpl): RegisterRepository
}