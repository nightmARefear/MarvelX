package com.alexgreece45.marvelx.util.extensions

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import com.google.accompanist.placeholder.PlaceholderDefaults
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer

fun Modifier.shimmer(shouldShimmer: Boolean = false) = composed {
    if (!shouldShimmer)
        this
    else
        this.placeholder(
            visible = true,
            color = PlaceholderDefaults.color(),
            highlight = PlaceholderHighlight.shimmer(
                highlightColor = PlaceholderDefaults.shimmerHighlightColor()
            )
        )
}
