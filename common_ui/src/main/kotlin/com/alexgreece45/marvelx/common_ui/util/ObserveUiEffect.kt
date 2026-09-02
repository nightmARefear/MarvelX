package com.alexgreece45.marvelx.common_ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.SingleObserverFlow
import com.alexgreece45.marvelx.core.common.util.flows.single_observer_flow.model.CollectorTag
import kotlinx.coroutines.withContext
import java.util.UUID

/**
 * Safely collects one-time events from a [SingleObserverFlow] in a lifecycle-aware manner.
 *
 * Collection automatically starts when the [LocalLifecycleOwner] reaches the [Lifecycle.State.STARTED]
 * state and pauses when it drops below it (e.g., when the screen is in the background).
 *
 * By strictly requiring a [SingleObserverFlow], this composable guarantees at compile-time that standard
 * state flows cannot be accidentally passed in, ensuring critical navigation or snackbar events
 * are never silently dropped.
 *
 * > **Note:** This function utilizes the provided [tag] to assist with multi-observer protection
 * > and traceability. It does not enforce the single-observer rule itself; rather, the underlying
 * > [SingleObserverFlow] handles the heavy lifting to prevent concurrent collections.
 *
 * @param T The type of UI effect being emitted.
 * @param flow The strict [SingleObserverFlow] containing the one-time events to collect.
 * @param tag An identifier (typically the screen name) used to improve exception and crash traceability.
 * @param onUiEffect The callback invoked for each emitted side effect value while the lifecycle is active.
 */
@Composable
fun <T> ObserveUiEffect(
    flow: SingleObserverFlow<T>,
    tag: String,
    onUiEffect: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    // Generate a unique tag combined with a UUID to prevent false-positive lock collisions.
    val uniqueTag = remember(tag) { "${tag}_${UUID.randomUUID()}" }

    LaunchedEffect(flow, lifecycleOwner.lifecycle, uniqueTag) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(CollectorTag(uniqueTag)) {
                flow.collect(onUiEffect)
            }
        }
    }
}