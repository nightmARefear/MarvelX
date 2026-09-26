package com.alexgreece45.marvelx.tools.extensions

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import com.alexgreece45.marvelx.tools.rule.CoroutineTestRule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.mockito.Mockito
import kotlin.time.Duration.Companion.milliseconds

fun CoroutineTestRule.runTest(block: suspend TestScope.() -> Unit): Unit =
    testScope.runTest { block() }

suspend fun <T> Flow<T>.runFlowTest(
    timeOut: Long? = null,
    block: suspend ReceiveTurbine<T>.() -> Unit
) {
    test(timeOut?.milliseconds) {
        block()
        cancelAndConsumeRemainingEvents()
    }
}

object MockitoHelper {
    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> uninitialized(): T = null as T
}
