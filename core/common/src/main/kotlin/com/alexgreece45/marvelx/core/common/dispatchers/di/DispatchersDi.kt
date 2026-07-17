package com.alexgreece45.marvelx.core.common.dispatchers.di

import com.alexgreece45.marvelx.core.common.dispatchers.model.DefaultDispatcher
import com.alexgreece45.marvelx.core.common.dispatchers.model.IoDispatcher
import com.alexgreece45.marvelx.core.common.dispatchers.model.MainDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Named
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

// Main
@Single
@MainDispatcher
fun provideMainDispatcherByAnnotation(): CoroutineContext = Dispatchers.Main

@Single
@Named("MainDispatcher")
fun provideMainDispatcherByString(): CoroutineContext = Dispatchers.Main

// Default
@Single
@DefaultDispatcher
fun provideDefaultDispatcherByAnnotation(): CoroutineContext = Dispatchers.Default

@Single
@Named("DefaultDispatcher")
fun provideDefaultDispatcherByString(): CoroutineContext = Dispatchers.Default

// IO
@Single
@IoDispatcher
fun provideIoDispatcherByAnnotation(): CoroutineContext = Dispatchers.IO

@Single
@Named("IoDispatcher")
fun provideIoDispatcherByString(): CoroutineContext = Dispatchers.IO
