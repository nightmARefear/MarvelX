package com.alexgreece45.marvelx.util.extensions

import android.graphics.Rect
import android.view.View

/**
 * Checks if this [View] is obscured by other views (including overlays from other apps)
 * and returns `true` if it is, otherwise `false`.
 *
 * @param obscurationThreshold  The fraction (between 0.0 and 1.0) of the View's area that must be
 * obscured to consider the View obscured. Defaults to 0.8 (80%).
 *
 * @return `true` if the View is obscured beyond the given threshold, `false` otherwise.
 */
fun View.isObscured(obscurationThreshold: Float = 0.8f): Boolean {
    val viewRect = Rect()
    val visibleRect = Rect()
    getGlobalVisibleRect(visibleRect) // Get visible rect considering overlaps
    getLocalVisibleRect(viewRect) // Get the view's local visible rect

    val intersectionRect = Rect()
    if (Rect.intersects(viewRect, visibleRect)) {
        intersectionRect.setIntersect(viewRect, visibleRect) // Calculate intersection
    }

    val intersectionArea = intersectionRect.width() * intersectionRect.height()
    val viewArea = viewRect.width() * viewRect.height()

    return intersectionArea < viewArea * obscurationThreshold
}