package com.alexgreece45.marvelx.di

import com.alexgreece45.marvelx.core.common.logger.AppLogger
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.mockito.Mockito

/**
 * This `object` provides utility functions for `Koin` - related operations.
 *
 * Only needed for classes that resolve dependencies through `KoinComponent` / `by inject()` (e.g.
 * `MainActivityViewModel`). Constructor-injected classes are instantiated directly with mocks.
 */
object KoinTestUtil {
    /**
     * Initializes a `KoinApplication` with the given [modules]. Also, [testModule] is automatically
     * provided. Duplicate [Module]s are only added once.
     */
    fun initKoin(vararg modules: Module) {
        startKoin {
            modules(
                modules.toList().plus(testModule).toSet().toList()
            )
        }
    }
}

/**
 * Base test module. `AppLoggerImpl` is internal to `:core:common`, hence a mock is provided.
 */
private val testModule = module {
    single<AppLogger> { Mockito.mock(AppLogger::class.java) }
}
