package com.alexgreece45.marvelx.ui.components.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * @param lifecycleOwner    The [LifecycleOwner] of the caller composable.
 * @param lifecycleEvent    The lifecycle event that [block] should be executed on.
 * @param block             The action(s) to be executed.
 */
@Composable
fun DisposableEffectWithLifecycle(
    lifecycleOwner: LifecycleOwner,
    lifecycleEvent: Lifecycle.Event,
    block: () -> Unit
) {
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