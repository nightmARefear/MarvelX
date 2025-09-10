package com.alexgreece45.marvelx.dispatchers

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

val dispatchersModule = module {
    single<CoroutineContext>(qualifier = named(MarvelXDispatchers.Main)) { Dispatchers.Main }
    single<CoroutineContext>(qualifier = named(MarvelXDispatchers.Default)) { Dispatchers.Default }
    single<CoroutineContext>(qualifier = named(MarvelXDispatchers.IO)) { Dispatchers.IO }
}
