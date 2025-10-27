package com.alexgreece45.marvelx.ui.components

import android.content.Context
import android.view.WindowManager
import com.alexgreece45.marvelx.util.extensions.getActivity

/**
 * Enforces the max brightness.
 */
fun enforceMaxBrightness(context: Context, isMaxBrightness: Boolean) {
    val activity = context.getActivity() ?: return
    val layoutParams = activity.window.attributes
    layoutParams.screenBrightness =
        if (isMaxBrightness) 1f else WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
    activity.window.attributes = layoutParams
}
