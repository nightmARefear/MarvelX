package com.alexgreece45.marvelx.di

import android.content.Context
import androidx.room.Room
import com.alexgreece45.marvelx.BuildConfig
import com.alexgreece45.marvelx.database.FavoriteCharactersDao
import com.alexgreece45.marvelx.database.FavoriteCharactersDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideFavoriteCharactersDatabase(@ApplicationContext context: Context): FavoriteCharactersDatabase =
        Room.databaseBuilder(
            context,
            FavoriteCharactersDatabase::class.java,
            BuildConfig.DATABASE_NAME
        ).build()

    @Provides
    @Singleton
    fun provideFavoriteCharactersDao(favoriteCharactersDatabase: FavoriteCharactersDatabase): FavoriteCharactersDao =
        favoriteCharactersDatabase.favoriteCharactersDao
}