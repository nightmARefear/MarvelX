package com.alexgreece45.marvelx.ui.components.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

/**
 * Collects values from a [Flow] in a lifecycle-aware manner. The collection is automatically paused
 * and resumed according to the lifecycle of the composable. Values are collected only when the
 * composable is at least in the `STARTED` state.
 *
 * @param flow          The [Flow] to collect values from.
 * @param onUiEffect    The callback to be invoked for each collected value.
 */
@Composable
fun <T> ObserveUiEffect(flow: Flow<T>, onUiEffect: (T) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(onUiEffect)
        }
    }
}

/**
 * Executes the given [block] when the lifecycle event is [lifecycleEvent].
 *
 * @param lifecycleEvent    The lifecycle event that [block] should be executed on.
 * @param block             The action(s) to be executed.
 */
@Composable
fun DisposableEffectWithLifecycle(
    lifecycleEvent: Lifecycle.Event,
    block: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == lifecycleEvent) {
                block()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

/**
 * Executes a [LaunchedEffect] only once even if configuration changes occur.
 *
 *  @param block    Code to be executed only once.
 */
@Composable
fun OneTimeLaunchedEffect(
    block: () -> Unit
) {
    var initialEffects by rememberSaveable { mutableStateOf(false) }
    if (!initialEffects) {
        LaunchedEffect(Unit) {
            initialEffects = true
            block()
        }
    }
}
