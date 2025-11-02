package com.alexgreece45.marvelx.dispatchers

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

// TODO: Fix injection.
val dispatchersModule = module {
    single<CoroutineContext>(qualifier = qualifier<MainDispatcher>()) { Dispatchers.Main }
    single<CoroutineContext>(qualifier = qualifier<DefaultDispatcher>()) { Dispatchers.Default }
    single<CoroutineContext>(qualifier = qualifier<IoDispatcher>()) { Dispatchers.IO }
    single<CoroutineContext>(qualifier = named(MarvelXDispatchers.IO)) { Dispatchers.IO }
}
