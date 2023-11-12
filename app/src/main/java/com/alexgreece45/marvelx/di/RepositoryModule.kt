package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.repository.CharacterRepository
import com.alexgreece45.marvelx.repository.CharacterRepositoryImpl
import com.alexgreece45.marvelx.repository.FavoriteCharactersRepository
import com.alexgreece45.marvelx.repository.FavoriteCharactersRepositoryImpl
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
    fun bindsUserRepository(impl: UserRepositoryImpl): UserRepository
}