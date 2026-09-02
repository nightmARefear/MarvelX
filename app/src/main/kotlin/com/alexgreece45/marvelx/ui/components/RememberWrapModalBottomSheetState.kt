package com.alexgreece45.marvelx.ui.components

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import com.alexgreece45.marvelx.ui.animation.modalBottomSheetShowAnimation

/**
 * Wrapper function that creates and remembers a [ModalBottomSheetState] with custom configuration
 * allowing smooth transitioning between multiple bottom sheets.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun rememberWrapModalBottomSheetState(
    initialValue: ModalBottomSheetValue = RememberWrapModalBottomSheetStateDefaults.InitialValue,
    animationSpec: AnimationSpec<Float> = RememberWrapModalBottomSheetStateDefaults.AnimationSpec,
    confirmValueChange: (ModalBottomSheetValue) -> Boolean = { true },
    skipHalfExpanded: Boolean = RememberWrapModalBottomSheetStateDefaults.SkipHalfExpanded,
): ModalBottomSheetState =
    rememberModalBottomSheetState(
        initialValue = initialValue,
        animationSpec = animationSpec,
        confirmValueChange = confirmValueChange,
        skipHalfExpanded = skipHalfExpanded
    )

/**
 * All the default values of [rememberWrapModalBottomSheetState]s.
 */
@Stable
object RememberWrapModalBottomSheetStateDefaults {

    /**
     * The default initial value.
     */
    @OptIn(ExperimentalMaterialApi::class)
    val InitialValue = ModalBottomSheetValue.Hidden

    /**
     * The default animation spec.
     */
    val AnimationSpec = modalBottomSheetShowAnimation()

    /**
     * The default skip half expanded.
     */
    @Suppress("ConstPropertyName")
    const val SkipHalfExpanded = true
}