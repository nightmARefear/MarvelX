package com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow

import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.model.CollectorTag
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

/**
 * A strict wrapper guaranteeing the underlying flow has multi-observer protection.
 */
class SingleObserverFlow<out T>(
    private val delegate: Flow<T>
) : Flow<T> by delegate

/**
 * Converts a Channel into a Flow that allows sequential collections (for lifecycle restarts)
 * but immediately crashes if a second concurrent collection is attempted.
 */
@OptIn(ExperimentalAtomicApi::class)
fun <T> Channel<T>.receiveAsSingleObserverFlow(): SingleObserverFlow<T> {
    // Holds the name of the current active collector. Null means it's available.
    val activeCollector = AtomicReference<String?>(null)

    val protectedFlow = this.receiveAsFlow()
        .onStart {
            // Read the invisible tag injected by ObserveUiEffect!
            val currentContextTag =
                currentCoroutineContext()[CollectorTag.Key]?.name ?: "UntaggedCollector"

            // Try to set the active collector from null -> currentContextTag
            if (!activeCollector.compareAndSet(
                    expectedValue = null,
                    newValue = currentContextTag
                )
            ) {

                // If it fails, we know exactly who the culprit is!
                val rogueCollector = activeCollector.load()
                throw IllegalStateException(
                    "CRITICAL: '$currentContextTag' tried to collect the effect flow, " +
                            "but '$rogueCollector' is already actively collecting it! " +
                            "You have overlapping collectors."
                )
            }
        }
        .onCompletion {
            // Release the lock when repeatOnLifecycle cancels it
            activeCollector.store(null)
        }
    return SingleObserverFlow(protectedFlow)
}