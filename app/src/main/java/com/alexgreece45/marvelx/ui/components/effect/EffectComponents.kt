package com.alexgreece45.marvelx.ui.components.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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