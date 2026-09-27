@file:OptIn(ExperimentalCoroutinesApi::class)

package com.alexgreece45.marvelx.tools.rule

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Replaces `Dispatchers.Main` with [testDispatcher]. Pass [testDispatcher] to every constructor
 * that expects an `@IoDispatcher` / `@DefaultDispatcher` `CoroutineContext`, so that all the
 * work runs on the [testScope]'s scheduler.
 */
class CoroutineTestRule(
    val testDispatcher: TestDispatcher = StandardTestDispatcher(),
    val testScope: TestScope = TestScope(testDispatcher)
) : TestWatcher() {
    override fun starting(description: Description?) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        Dispatchers.resetMain()
    }
}
