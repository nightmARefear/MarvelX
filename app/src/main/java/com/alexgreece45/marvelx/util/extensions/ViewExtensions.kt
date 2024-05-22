package com.alexgreece45.marvelx.util.extensions

import android.graphics.Rect
import android.view.View

/**
 * Checks if this [View] is obscured and returns `true` if it is, otherwise `false`.
 */
fun View.isObscured(): Boolean {
    val rect = Rect()
    this.getWindowVisibleDisplayFrame(rect)

    val screenHeight = this.height
    val screenWidth = this.width
    val heightDiff = screenHeight - rect.height()
    val widthDiff = screenWidth - rect.width()

    return heightDiff > 0 || widthDiff > 0
}