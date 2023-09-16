package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.util.FormValidator
import com.alexgreece45.marvelx.util.FormValidatorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
interface FormValidatorModule {

    @Binds
    @ViewModelScoped
    fun bindsFormValidator(impl: FormValidatorImpl): FormValidator
}