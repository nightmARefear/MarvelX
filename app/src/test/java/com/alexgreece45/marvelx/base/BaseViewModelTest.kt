package com.alexgreece45.marvelx.base

import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

/**
 * The base test class of all the `ViewModel`s. It contains [setUp] where each subclass must override
 * it to provide start-up functionality, and [after] where `Koin` is stopped.
 */
internal abstract class BaseViewModelTest : KoinTest {
    /**
     * The initialization actions invoked before the execution of the first test.
     */
    @Before
    abstract fun setUp()

    /**
     * The destructive actions invoked after the completion of the last test. It stops `Koin` (a
     * no-op when it was never started) and removes all the mocks created from `mockk`.
     */
    @After
    fun after() {
        stopKoin()
        unmockkAll()
    }
}
