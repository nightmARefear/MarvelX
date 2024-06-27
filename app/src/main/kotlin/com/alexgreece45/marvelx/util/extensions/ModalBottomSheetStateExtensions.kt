package com.alexgreece45.marvelx.util.extensions

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState

/**
 * Hides this modal bottom sheet instance only if it's visible. Avoid using the native
 * [ModalBottomSheetState.hide]. Use this function instead.
 */
@OptIn(ExperimentalMaterialApi::class)
suspend fun ModalBottomSheetState.hideIfVisible() {
    if (this.isVisible) {
        this.hide()
    }
}
