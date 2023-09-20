package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.domain.repositories.CharacterRepository
import com.alexgreece45.marvelx.domain.repositories.CharacterRepositoryImpl
import com.alexgreece45.marvelx.domain.repositories.FavoriteCharactersRepository
import com.alexgreece45.marvelx.domain.repositories.FavoriteCharactersRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface RepositoryModule {

    @Binds
    @ViewModelScoped
    fun bindsCharacterRepository(impl: CharacterRepositoryImpl): CharacterRepository

    @Binds
    @ViewModelScoped
    fun bindsFavoriteCharactersRepository(impl: FavoriteCharactersRepositoryImpl): FavoriteCharactersRepository
}