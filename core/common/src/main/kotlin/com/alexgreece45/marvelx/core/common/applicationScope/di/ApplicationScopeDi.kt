package com.alexgreece45.marvelx.core.common.applicationScope.di

import com.alexgreece45.marvelx.core.common.applicationScope.model.ApplicationScope
import com.alexgreece45.marvelx.core.common.dispatchers.model.MainDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.Single
import kotlin.coroutines.CoroutineContext

@Single
@ApplicationScope
internal fun provideApplicationScope(@MainDispatcher coroutineContext: CoroutineContext): CoroutineScope =
    CoroutineScope(SupervisorJob() + coroutineContext)
