package com.alexgreece45.marvelx.util.extensions

import androidx.compose.material.ModalBottomSheetState

/**
 * Hides this modal bottom sheet instance only if it's visible. Avoid using the native
 * [ModalBottomSheetState.hide]. Use this function instead.
 */
suspend fun ModalBottomSheetState.hideIfVisible() {
    if (this.isVisible) {
        this.hide()
    }
}

suspend fun ModalBottomSheetState.showIfHidden() {
    if (!this.isVisible) {
        this.show()
    }
}