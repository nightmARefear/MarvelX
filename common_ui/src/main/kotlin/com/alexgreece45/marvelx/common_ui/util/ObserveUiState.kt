package com.alexgreece45.marvelx.common_ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.StateFlow

/**
 * Collects values from a [StateFlow] in a lifecycle-aware manner. The collection is automatically paused
 * and resumed according to the lifecycle of the composable. Values are collected only when the
 * composable is at least in the `STARTED` state.
 *
 * @param flow          The [StateFlow] to collect values from.
 * @param onUiEffect    The callback to be invoked for each collected value.
 */
@Composable
fun <T> ObserveUiState(
    flow: StateFlow<T>,
    onUiEffect: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(flow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(onUiEffect)
        }
    }
}